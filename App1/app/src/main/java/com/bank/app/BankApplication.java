package com.bank.app;

import jakarta.persistence.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.List;

@SpringBootApplication
@EnableJpaRepositories
public class BankApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankApplication.class, args);
	}
}

@Entity
class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String username;
	private String password;

	// Getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}

@Entity
class BankAccount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String iban;
	private String currency;
	private double balance;
	private AccountType accountType;
	private double interest;

	// Getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public double getInterest() {
		return interest;
	}

	public void setInterest(double interest) {
		this.interest = interest;
	}


	public CreditCard getCreditCard() {
		//gets card information from client through the database
		//if the user is in the database then it returns the information for the card
		//otherwise nothing.
		return null;
	}

	public Object getUser() {
		//gets the user from the database
		//if the user doesn't exist then null.
		return null;
	}
}
@Entity
class TechnicalAccount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(mappedBy = "technicalAccount")
	protected CreditCard creditCard; // Link to the set credit card

	// Fields to Technical Account
	private double balance; // Current balance of the technical account

	// Constructors
	public TechnicalAccount() {
		// Default constructor
	}
	public class InsufficientFundsException extends Exception {
		public InsufficientFundsException(String message) {
			super(message);
		}
	}
	public TechnicalAccount(CreditCard creditCard, double initialBalance) {
		this.creditCard = creditCard;
		this.balance = initialBalance;
	}

	// Getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	// Methods for technical account features
	public void processTransaction(Transaction transaction) throws InsufficientFundsException {
		// Process a transaction and update the balance in real-time
		// Ensures that transaction data is ok and processed according to set rules
		// Update the balance based on the transaction amount and type deb-cred
		// Audit trail of transactions for transparency & security

		// Example logic:
		if (transaction.getAmount() >= 0) {
			// Credit transaction
			balance += transaction.getAmount();
		} else {
			// Debit transaction
			double debitAmount = Math.abs(transaction.getAmount());
			if (balance >= debitAmount) {
				balance -= debitAmount;
			} else throw new InsufficientFundsException("Insufficient funds in the technical account.");
		}

		// Audit trail
		String transactionType = transaction.getAmount() >= 0 ? "Credit" : "Debit";
		String auditLog = "Transaction Type: " + transactionType +
				", Amount: " + transaction.getAmount() +
				", New Balance: " + balance;

		// Store the audit trail for transparency & security
		System.out.println(auditLog);
	}
}

@Entity
class DebitCard {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

	@OneToOne
	@JoinColumn(name = "bank_account_id")
	private BankAccount bankAccount;

	private String cardNumber; // Debit card number
	private String cardHolderName; // Name of the card-holder
	private String expirationDate; // Expiration date of the card
	private boolean isActive; // Debit card activation status

	// Constructors
	public DebitCard() {
		// Default constructor
	}

	public DebitCard(User user, BankAccount bankAccount, String cardNumber, String cardHolderName, String expirationDate) {
		this.user = user;
		this.bankAccount = bankAccount;
		this.cardNumber = cardNumber;
		this.cardHolderName = cardHolderName;
		this.expirationDate = expirationDate;
		this.isActive = true; // Debit card is active by default
	}

	// Getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public BankAccount getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCardHolderName() {
		return cardHolderName;
	}

	public void setCardHolderName(String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}


	public boolean isAccessToFundsAvailable() {
		// If the linked bank account has sufficient funds
		return bankAccount != null && bankAccount.getBalance() > 0;
	}

	public boolean isInterestCharged() {
		// Debit cards do not accrue interest charges
		return false;
	}

	public boolean isTransactionPINBased() {
		// Logic to determine if a transaction is PIN-based
		// This can depend on the payment processing system
		// For example, if the transaction amount is less than a threshold, it's PIN-based
		return bankAccount != null && bankAccount.getBalance() >= 100;
	}

	public boolean isTransactionSignatureBased() {
		// Logic to determine if transaction is signature-based
		// This can depend on the payment processing system
		// If the transaction amount is > than a threshold, it's signature-based
		return bankAccount != null && bankAccount.getBalance() >= 500;
	}

	public boolean isSpendingControlled(double transactionAmount) {
		// Check if transaction amount is within the available funds in bnk account
		return bankAccount != null && bankAccount.getBalance() >= transactionAmount;
	}

	public void reportLostOrStolenCard() {
		if (isActive) {
			isActive = false; // Disable card
		}
	}
}
@Entity
class CreditCard {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private double monthlyIncome;

	private double creditLimit; // Added credit-limit field

	private double apr; // Added apr field

	private double minimumPayment; // Added minimum-payment field

	private int gracePeriod; // Added grace-period field

	private String rewards; // Added rewards & benefits field

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@OneToOne(mappedBy = "technicalAccount")
	private TechnicalAccount technicalAccount;

	// Constructors
	public CreditCard(){
	}

	public CreditCard(double monthlyIncome, double creditLimit, double apr, double minimumPayment, int gracePeriod, String rewards) {
		this.monthlyIncome = monthlyIncome;
		this.creditLimit = creditLimit;
		this.apr = apr;
		this.minimumPayment = minimumPayment;
		this.gracePeriod = gracePeriod;
		this.rewards = rewards;
	}
	//Method to calculate interest for a transaction
	public double calculateTransactionInterest(double transactionAmount) {
		// Transaction amount is greater than the minimum payment
		if (transactionAmount <= minimumPayment) {
			return 0.0;
		}

		// Calculating daily interest rate
		double dailyInterestRate = (apr / 365.0) / 100.0; // Assuming 365 days in a year

		// Calculating interest for the transaction amount
		double interest = 0.0;

		// Check if the transaction is within the grace period
		if (gracePeriod > 0) {
			// Calculating interest within the grace period
			interest = 0.0;
		} else {
			// Calculating interest beyond the grace period
			int daysBeyondGracePeriod = Math.max(0, -gracePeriod);
			double principal = transactionAmount - minimumPayment;

			for (int day = 1; day <= daysBeyondGracePeriod; day++) {
				// Apply daily compounding formula with APR: A = P(1 + r)^t
				// A- Future value (including interest)
				// P-  Principal (transaction amount - minimum payment)
				// r-  Daily interest rate
				// t- Number of days beyond grace period
				interest += principal * Math.pow(1 + dailyInterestRate, day);
			}

			// Subtract the principal amount to get the interest alone
			interest -= principal;
		}

		return interest;
	}

	// Getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public double getMonthlyIncome() {
		return monthlyIncome;
	}

	public void setMonthlyIncome(double monthlyIncome) {
		this.monthlyIncome = monthlyIncome;
	}

	public double getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(double creditLimit) {
		this.creditLimit = creditLimit;
	}

	public double getApr() {
		return apr;
	}

	public void setApr(double apr) {
		this.apr = apr;
	}

	public double getMinimumPayment() {
		return minimumPayment;
	}

	public void setMinimumPayment(double minimumPayment) {
		this.minimumPayment = minimumPayment;
	}

	public int getGracePeriod() {
		return gracePeriod;
	}

	public void setGracePeriod(int gracePeriod) {
		this.gracePeriod = gracePeriod;
	}

	public String getRewards() {
		return rewards;
	}

	public void setRewards(String rewards) {
		this.rewards = rewards;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public TechnicalAccount getTechnicalAccount() {
		return technicalAccount;
	}

	public void setTechnicalAccount(TechnicalAccount technicalAccount) {
		this.technicalAccount = technicalAccount;
	}

}



@Entity
class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "sender_account_id")
	private BankAccount senderAccount; // Sender bank account

	@ManyToOne
	@JoinColumn(name = "receiver_account_id")
	private BankAccount receiverAccount; // Receiver bank account
	public enum TransactionType {
		DEBIT,
		CREDIT;

		public static TransactionType fromString(String type) {
			if (type != null) {
				for (TransactionType transactionType : TransactionType.values()) {
					if (type.equalsIgnoreCase(transactionType.name())) {
						return transactionType;
					}
				}
			}
			return null;
		}
	}
	public class InsufficientFundsException extends Exception {
		public InsufficientFundsException(String message) {
			super(message);
		}
	}



	private double amount; // Transaction amount
	private String currency; // Curency of the transaction
	private TransactionType type; // Transaction type (DEBIT or CREDIT)
	private Date transactionDate; // Date of transaction

	public void executeTransaction() throws InsufficientFundsException {
		// Implement logic to execute the transaction based on the specified rules:
		// 1. Balance Availability
		// 2. Account Linkage
		// 3. Interest Calculation for Technical Account

		if (type == TransactionType.DEBIT) {
			// Debit transaction (amount subtracted from sender's account)
			if (senderAccount != null && senderAccount.getBalance() >= amount) {
				// Sufficient balance available
				senderAccount.setBalance(senderAccount.getBalance() - amount);
				receiverAccount.setBalance(receiverAccount.getBalance() + amount);
			} else if (senderAccount != null && senderAccount.getAccountType() == AccountType.TECHNICAL) {
				// Insufficient funds, but it's a technical account (Credit Card)
				senderAccount.setBalance(senderAccount.getBalance() - amount); // Balance becomes negative
				receiverAccount.setBalance(receiverAccount.getBalance() + amount);
				// Calculate and add interest to the subtracted amount (implement this logic)
				double transactionInterest = senderAccount.getCreditCard().calculateTransactionInterest(amount);
				senderAccount.setBalance(senderAccount.getBalance() - transactionInterest);
			} else {
				// Insufficient funds, transaction not allowed
				throw new InsufficientFundsException("Insufficient funds for the debit transaction.");
			}
		} else if (type == TransactionType.CREDIT) {
			// Credit transaction (amount added to receiver's account)
			if (receiverAccount != null) {
				receiverAccount.setBalance(receiverAccount.getBalance() + amount);
				senderAccount.setBalance(senderAccount.getBalance() - amount);
			}
		}
	}

	public double getAmount() {
		return 0;
	}
}


enum AccountType {
	CURRENT,
	TECHNICAL
}
interface UserRepository extends JpaRepository<User, Long> {
}

interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
	List<BankAccount> findByAccountType(AccountType accountType);

	List<BankAccount> findByUser(User currentUser);
}
@Repository
interface TransactionRepository extends JpaRepository<Transaction, Long> {
	List<Transaction> findBySenderAccountOrReceiverAccount(BankAccount senderAccount, BankAccount receiverAccount);
}
@Controller
@RequestMapping("/admin")
class AdminController {
	private final UserRepository userRepository;

	public AdminController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping("/createUser")
	public String createUser() {
		// Implementing logic to create a user
		return "redirect:/";
	}
}

@Controller
@RequestMapping("/teller")
class TellerController {
	private final BankAccountRepository bankAccountRepository;

	public TellerController(BankAccountRepository bankAccountRepository) {
		this.bankAccountRepository = bankAccountRepository;
	}

	@GetMapping("/requestAccount")
	public String requestAccount(Model model) {
		// Implementing logic to handle account requests
		List<BankAccount> pendingAccounts = bankAccountRepository.findByAccountType(AccountType.CURRENT);
		model.addAttribute("pendingAccounts", pendingAccounts);
		return "account_request";
	}


	@PostMapping("/approveAccount/{accountId}")
	public String approveAccount(@PathVariable Long accountId) {
		// Implementing logic to approve a bank account
		return "redirect:/teller/requestAccount";
	}
}

@Controller
@RequestMapping("/client")
class ClientController {
	private final UserRepository userRepository;
	private final BankAccountRepository bankAccountRepository;
	private final TransactionRepository transactionRepository;


	public ClientController(UserRepository userRepository, BankAccountRepository bankAccountRepository, TransactionRepository transactionRepository) {
		this.userRepository = userRepository;
		this.bankAccountRepository = bankAccountRepository;
		this.transactionRepository = transactionRepository;
	}

	@GetMapping("/accounts")
	public String viewAccounts(Model model) {
		// Retrieve the current user

		org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = (User) authentication.getPrincipal();

		// Implementing logic to retrieve client accounts based on the current user
		List<BankAccount> clientAccounts = bankAccountRepository.findByUser(currentUser);
		model.addAttribute("clientAccounts", clientAccounts);
		return "client_accounts";
	}

	@GetMapping("/transactions/{accountId}")
	public String viewTransactions(@PathVariable Long accountId, Model model) {
		// Retrieve the current user from the authentication context
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.isAuthenticated()) {
			Object principal = authentication.getPrincipal();

			if (principal instanceof UserDetails) {
				UserDetails userDetails = (UserDetails) principal;
				String currentUsername = userDetails.getUsername();

				// Now we have the username of the currently authenticated user (currentUsername)
				// We can use this username to retrieve user-specific data or validate ownership

				// Implementing logic, such as checking if the account belongs to the user
				BankAccount account = bankAccountRepository.findById(accountId).orElse(null);
				//the getUsername is supposed to be collected from the user through the web application and database
				/*if (account != null && account.getUser().getUsername().equals(currentUsername)) {
					List<Transaction> transactions = transactionRepository.findBySenderAccountOrReceiverAccount(account, account);
					model.addAttribute("transactions", transactions);
					return "client_transactions";
				}*/
			}
		}

		// If not authenticated or account doesn't belong to the current user, redirect
		return "redirect:/client/accounts";
	}
}


