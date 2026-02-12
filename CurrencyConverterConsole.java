import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CurrencyConverterConsole {
    // Exchange rates relative to USD (1 USD = X currency)
    private static final Map<String, Double> exchangeRates = new HashMap<>();
    
    static {
        // Initialize exchange rates (as of example - you should update these)
        exchangeRates.put("USD", 1.0);      // US Dollar
        exchangeRates.put("EUR", 0.92);     // Euro
        exchangeRates.put("GBP", 0.79);     // British Pound
        exchangeRates.put("JPY", 149.50);   // Japanese Yen
        exchangeRates.put("CNY", 7.24);     // Chinese Yuan
        exchangeRates.put("INR", 83.12);    // Indian Rupee
        exchangeRates.put("CAD", 1.36);     // Canadian Dollar
        exchangeRates.put("AUD", 1.53);     // Australian Dollar
    }
    
    /**
     * Converts amount from one currency to another
     * @param amount The amount to convert
     * @param fromCurrency Source currency code
     * @param toCurrency Target currency code
     * @return Converted amount
     */
    public static double convert(double amount, String fromCurrency, String toCurrency) {
        // First convert to USD, then to target currency
        double amountInUSD = amount / exchangeRates.get(fromCurrency);
        double convertedAmount = amountInUSD * exchangeRates.get(toCurrency);
        return convertedAmount;
    }
    
    /**
     * Displays all available currencies
     */
    public static void displayAvailableCurrencies() {
        System.out.println("\nAvailable Currencies:");
        System.out.println("---------------------");
        for (String currency : exchangeRates.keySet()) {
            System.out.println(currency);
        }
    }
    
    /**
     * Validates if a currency code exists
     */
    public static boolean isValidCurrency(String currency) {
        return exchangeRates.containsKey(currency.toUpperCase());
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=================================");
        System.out.println("   CURRENCY CONVERTER APP");
        System.out.println("=================================");
        
        boolean continueConverting = true;
        
        while (continueConverting) {
            displayAvailableCurrencies();
            
            // Get source currency
            String fromCurrency = "";
            while (true) {
                System.out.print("\nEnter source currency code: ");
                fromCurrency = scanner.nextLine().toUpperCase().trim();
                
                if (isValidCurrency(fromCurrency)) {
                    break;
                } else {
                    System.out.println("Invalid currency code. Please try again.");
                }
            }
            
            // Get target currency
            String toCurrency = "";
            while (true) {
                System.out.print("Enter target currency code: ");
                toCurrency = scanner.nextLine().toUpperCase().trim();
                
                if (isValidCurrency(toCurrency)) {
                    break;
                } else {
                    System.out.println("Invalid currency code. Please try again.");
                }
            }
            
            // Get amount
            double amount = 0;
            while (true) {
                try {
                    System.out.print("Enter amount to convert: ");
                    amount = Double.parseDouble(scanner.nextLine());
                    
                    if (amount >= 0) {
                        break;
                    } else {
                        System.out.println("Please enter a positive number.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            }
            
            // Perform conversion
            double result = convert(amount, fromCurrency, toCurrency);
            
            // Display result
            System.out.println("\n=================================");
            System.out.printf("%.2f %s = %.2f %s%n", amount, fromCurrency, result, toCurrency);
            System.out.println("=================================");
            
            // Ask if user wants to continue
            System.out.print("\nDo you want to convert another amount? (yes/no): ");
            String response = scanner.nextLine().trim().toLowerCase();
            continueConverting = response.equals("yes") || response.equals("y");
        }
        
        System.out.println("\nThank you for using Currency Converter!");
        scanner.close();
    }
}