import java.util.Scanner;

/**
 * Simple Currency Converter
 * This is a basic version focusing on core conversion logic
 */
public class SimpleCurrencyConverter {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Define exchange rates (relative to USD)
        double usdRate = 1.0;
        double eurRate = 0.92;
        double gbpRate = 0.79;
        double jpyRate = 149.50;
        double inrRate = 83.12;
        
        System.out.println("===== Currency Converter =====");
        System.out.println("1. USD (US Dollar)");
        System.out.println("2. EUR (Euro)");
        System.out.println("3. GBP (British Pound)");
        System.out.println("4. JPY (Japanese Yen)");
        System.out.println("5. INR (Indian Rupee)");
        System.out.println("==============================");
        
        // Get source currency
        System.out.print("\nSelect source currency (1-5): ");
        int fromChoice = scanner.nextInt();
        
        // Get target currency
        System.out.print("Select target currency (1-5): ");
        int toChoice = scanner.nextInt();
        
        // Get amount
        System.out.print("Enter amount to convert: ");
        double amount = scanner.nextDouble();
        
        // Get exchange rates for selected currencies
        double fromRate = 0, toRate = 0;
        String fromCurrency = "", toCurrency = "";
        
        // Determine FROM currency
        switch (fromChoice) {
            case 1: fromRate = usdRate; fromCurrency = "USD"; break;
            case 2: fromRate = eurRate; fromCurrency = "EUR"; break;
            case 3: fromRate = gbpRate; fromCurrency = "GBP"; break;
            case 4: fromRate = jpyRate; fromCurrency = "JPY"; break;
            case 5: fromRate = inrRate; fromCurrency = "INR"; break;
            default: 
                System.out.println("Invalid choice!");
                scanner.close();
                return;
        }
        
        // Determine TO currency
        switch (toChoice) {
            case 1: toRate = usdRate; toCurrency = "USD"; break;
            case 2: toRate = eurRate; toCurrency = "EUR"; break;
            case 3: toRate = gbpRate; toCurrency = "GBP"; break;
            case 4: toRate = jpyRate; toCurrency = "JPY"; break;
            case 5: toRate = inrRate; toCurrency = "INR"; break;
            default: 
                System.out.println("Invalid choice!");
                scanner.close();
                return;
        }
        
        // Convert: First to USD, then to target currency
        double amountInUSD = amount / fromRate;
        double convertedAmount = amountInUSD * toRate;
        
        // Display result
        System.out.println("\n==============================");
        System.out.printf("%.2f %s = %.2f %s%n", amount, fromCurrency, convertedAmount, toCurrency);
        System.out.println("==============================");
        
        scanner.close();
    }
}