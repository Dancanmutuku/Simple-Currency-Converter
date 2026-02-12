import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

/**
 * Currency Converter using Exchange Rate API
 * Supports conversion between any currencies with real-time rates
 * 
 * API Used: https://api.exchangerate-api.com (Free tier available)
 * Alternative APIs: https://api.frankfurter.app, https://openexchangerates.org
 */
public class APICurrencyConverter {
    
    // Free API endpoint - no API key required for basic usage
    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/";
    
    // Alternative API (European Central Bank rates)
    // private static final String API_URL = "https://api.frankfurter.app/latest?from=";
    
    /**
     * Fetches exchange rates from API for a given base currency
     * @param baseCurrency The base currency code (e.g., "USD")
     * @return JSONObject containing exchange rates
     */
    public static JSONObject getExchangeRates(String baseCurrency) throws Exception {
        String urlString = API_URL + baseCurrency;
        URL url = new URL(urlString);
        
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        
        int responseCode = conn.getResponseCode();
        
        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream())
            );
            StringBuilder response = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            return new JSONObject(response.toString());
        } else {
            throw new Exception("API request failed with response code: " + responseCode);
        }
    }
    
    /**
     * Converts amount from one currency to another using live rates
     * @param amount Amount to convert
     * @param fromCurrency Source currency code
     * @param toCurrency Target currency code
     * @return Converted amount
     */
    public static double convertCurrency(double amount, String fromCurrency, String toCurrency) 
            throws Exception {
        
        // If same currency, return same amount
        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            return amount;
        }
        
        // Get exchange rates with fromCurrency as base
        JSONObject data = getExchangeRates(fromCurrency.toUpperCase());
        JSONObject rates = data.getJSONObject("rates");
        
        // Get the exchange rate for target currency
        if (!rates.has(toCurrency.toUpperCase())) {
            throw new Exception("Currency code not found: " + toCurrency);
        }
        
        double exchangeRate = rates.getDouble(toCurrency.toUpperCase());
        return amount * exchangeRate;
    }
    
    /**
     * Displays a list of available currencies from the API
     */
    public static void displayAvailableCurrencies(String baseCurrency) {
        try {
            JSONObject data = getExchangeRates(baseCurrency);
            JSONObject rates = data.getJSONObject("rates");
            
            System.out.println("\n===== Available Currencies =====");
            System.out.println("(Showing sample - " + rates.length() + " currencies available)");
            
            int count = 0;
            for (String key : rates.keySet()) {
                System.out.print(key + "  ");
                count++;
                if (count % 10 == 0) {
                    System.out.println();
                }
                if (count >= 50) { // Show first 50 currencies
                    System.out.println("\n... and " + (rates.length() - 50) + " more");
                    break;
                }
            }
            System.out.println("\n================================");
            
        } catch (Exception e) {
            System.out.println("Error fetching currency list: " + e.getMessage());
        }
    }
    
    /**
     * Validates if a currency code is valid by checking API
     */
    public static boolean isValidCurrency(String currencyCode) {
        try {
            JSONObject data = getExchangeRates("USD");
            JSONObject rates = data.getJSONObject("rates");
            return rates.has(currencyCode.toUpperCase()) || 
                   currencyCode.equalsIgnoreCase("USD");
        } catch (Exception e) {
            return false;
        }
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("==========================================");
        System.out.println("   LIVE CURRENCY CONVERTER (API-Based)");
        System.out.println("==========================================");
        System.out.println("Convert between ANY world currencies!");
        System.out.println("Using real-time exchange rates");
        System.out.println();
        
        boolean continueConverting = true;
        
        while (continueConverting) {
            try {
                // Option to view available currencies
                System.out.print("View available currencies? (yes/no): ");
                String viewCurrencies = scanner.nextLine().trim().toLowerCase();
                if (viewCurrencies.equals("yes") || viewCurrencies.equals("y")) {
                    displayAvailableCurrencies("USD");
                }
                
                // Get source currency
                String fromCurrency = "";
                while (true) {
                    System.out.print("\nEnter source currency code (e.g., USD, EUR, GBP): ");
                    fromCurrency = scanner.nextLine().toUpperCase().trim();
                    
                    if (fromCurrency.length() == 3) {
                        System.out.print("Validating currency code... ");
                        if (isValidCurrency(fromCurrency)) {
                            System.out.println("✓ Valid");
                            break;
                        } else {
                            System.out.println("✗ Invalid");
                            System.out.println("Please enter a valid 3-letter currency code.");
                        }
                    } else {
                        System.out.println("Currency code must be 3 letters (e.g., USD, EUR, JPY)");
                    }
                }
                
                // Get target currency
                String toCurrency = "";
                while (true) {
                    System.out.print("Enter target currency code (e.g., USD, EUR, GBP): ");
                    toCurrency = scanner.nextLine().toUpperCase().trim();
                    
                    if (toCurrency.length() == 3) {
                        System.out.print("Validating currency code... ");
                        if (isValidCurrency(toCurrency)) {
                            System.out.println("✓ Valid");
                            break;
                        } else {
                            System.out.println("✗ Invalid");
                            System.out.println("Please enter a valid 3-letter currency code.");
                        }
                    } else {
                        System.out.println("Currency code must be 3 letters (e.g., USD, EUR, JPY)");
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
                System.out.println("\nFetching live exchange rates...");
                double result = convertCurrency(amount, fromCurrency, toCurrency);
                
                // Display result
                System.out.println("\n==========================================");
                System.out.printf("%.2f %s = %.2f %s%n", amount, fromCurrency, result, toCurrency);
                System.out.printf("Exchange Rate: 1 %s = %.6f %s%n", 
                    fromCurrency, result/amount, toCurrency);
                System.out.println("==========================================");
                
            } catch (Exception e) {
                System.out.println("\n❌ Error: " + e.getMessage());
                System.out.println("Please check your internet connection and try again.");
            }
            
            // Ask if user wants to continue
            System.out.print("\nConvert another amount? (yes/no): ");
            String response = scanner.nextLine().trim().toLowerCase();
            continueConverting = response.equals("yes") || response.equals("y");
        }
        
        System.out.println("\nThank you for using Live Currency Converter!");
        scanner.close();
    }
}