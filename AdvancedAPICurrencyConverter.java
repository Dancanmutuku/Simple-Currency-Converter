import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import org.json.JSONObject;

/**
 * Advanced API Currency Converter with Multiple API Support
 * Features:
 * - Multiple API endpoints with automatic fallback
 * - Caching to reduce API calls
 * - Support for 150+ currencies
 * - Error handling and retry logic
 */
public class AdvancedAPICurrencyConverter {
    
    // Multiple API endpoints for redundancy
    private static final String[] API_ENDPOINTS = {
        "https://api.exchangerate-api.com/v4/latest/",
        "https://api.frankfurter.app/latest?from="
    };
    
    private static int currentAPIIndex = 0;
    private static Map<String, CachedRates> rateCache = new HashMap<>();
    private static final long CACHE_DURATION = 3600000; // 1 hour in milliseconds
    
    /**
     * Inner class to store cached exchange rates with timestamp
     */
    static class CachedRates {
        JSONObject rates;
        long timestamp;
        
        CachedRates(JSONObject rates, long timestamp) {
            this.rates = rates;
            this.timestamp = timestamp;
        }
        
        boolean isExpired() {
            return (System.currentTimeMillis() - timestamp) > CACHE_DURATION;
        }
    }
    
    /**
     * Fetches exchange rates with automatic API fallback
     */
    public static JSONObject getExchangeRates(String baseCurrency) throws Exception {
        // Check cache first
        if (rateCache.containsKey(baseCurrency)) {
            CachedRates cached = rateCache.get(baseCurrency);
            if (!cached.isExpired()) {
                System.out.println("✓ Using cached rates (fresh)");
                return cached.rates;
            }
        }
        
        Exception lastException = null;
        
        // Try each API endpoint
        for (int i = 0; i < API_ENDPOINTS.length; i++) {
            try {
                String apiUrl = API_ENDPOINTS[currentAPIIndex] + baseCurrency;
                JSONObject data = fetchFromAPI(apiUrl);
                
                // Cache the result
                rateCache.put(baseCurrency, new CachedRates(data, System.currentTimeMillis()));
                
                System.out.println("✓ Fetched fresh rates from API " + (currentAPIIndex + 1));
                return data;
                
            } catch (Exception e) {
                lastException = e;
                System.out.println("✗ API " + (currentAPIIndex + 1) + " failed, trying next...");
                currentAPIIndex = (currentAPIIndex + 1) % API_ENDPOINTS.length;
            }
        }
        
        throw new Exception("All API endpoints failed: " + lastException.getMessage());
    }
    
    /**
     * Fetches data from API endpoint
     */
    private static JSONObject fetchFromAPI(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        
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
            throw new Exception("HTTP " + responseCode);
        }
    }
    
    /**
     * Converts currency with enhanced error handling
     */
    public static double convertCurrency(double amount, String fromCurrency, String toCurrency) 
            throws Exception {
        
        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            return amount;
        }
        
        JSONObject data = getExchangeRates(fromCurrency.toUpperCase());
        JSONObject rates = data.getJSONObject("rates");
        
        if (!rates.has(toCurrency.toUpperCase())) {
            throw new Exception("Currency code not supported: " + toCurrency);
        }
        
        double exchangeRate = rates.getDouble(toCurrency.toUpperCase());
        return amount * exchangeRate;
    }
    
    /**
     * Gets all available currencies
     */
    public static List<String> getAllCurrencies() throws Exception {
        JSONObject data = getExchangeRates("USD");
        JSONObject rates = data.getJSONObject("rates");
        
        List<String> currencies = new ArrayList<>();
        currencies.add("USD");
        
        for (String key : rates.keySet()) {
            currencies.add(key);
        }
        
        Collections.sort(currencies);
        return currencies;
    }
    
    /**
     * Displays popular currencies with their names
     */
    public static void displayPopularCurrencies() {
        System.out.println("\n===== Popular Currencies =====");
        String[][] popular = {
            {"USD", "US Dollar"},
            {"EUR", "Euro"},
            {"GBP", "British Pound"},
            {"JPY", "Japanese Yen"},
            {"CHF", "Swiss Franc"},
            {"CAD", "Canadian Dollar"},
            {"AUD", "Australian Dollar"},
            {"CNY", "Chinese Yuan"},
            {"INR", "Indian Rupee"},
            {"KRW", "South Korean Won"},
            {"BRL", "Brazilian Real"},
            {"MXN", "Mexican Peso"},
            {"ZAR", "South African Rand"},
            {"SGD", "Singapore Dollar"},
            {"HKD", "Hong Kong Dollar"}
        };
        
        for (String[] curr : popular) {
            System.out.printf("%-5s - %s%n", curr[0], curr[1]);
        }
        System.out.println("==============================");
        System.out.println("(150+ currencies supported)");
    }
    
    /**
     * Batch conversion - convert one amount to multiple currencies
     */
    public static void batchConvert(double amount, String fromCurrency, String[] toCurrencies) 
            throws Exception {
        
        System.out.println("\n===== Batch Conversion =====");
        System.out.printf("Converting %.2f %s to:%n", amount, fromCurrency);
        System.out.println("============================");
        
        for (String toCurrency : toCurrencies) {
            try {
                double result = convertCurrency(amount, fromCurrency, toCurrency);
                System.out.printf("%-5s = %12.2f%n", toCurrency, result);
            } catch (Exception e) {
                System.out.printf("%-5s = Error: %s%n", toCurrency, e.getMessage());
            }
        }
    }
    
    /**
     * Compare exchange rates over time (if historical data available)
     */
    public static void displayExchangeRate(String fromCurrency, String toCurrency) 
            throws Exception {
        
        double rate = convertCurrency(1.0, fromCurrency, toCurrency);
        double reverseRate = convertCurrency(1.0, toCurrency, fromCurrency);
        
        System.out.println("\n===== Exchange Rate Info =====");
        System.out.printf("1 %s = %.6f %s%n", fromCurrency, rate, toCurrency);
        System.out.printf("1 %s = %.6f %s%n", toCurrency, reverseRate, fromCurrency);
        System.out.println("==============================");
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("============================================");
        System.out.println("  ADVANCED CURRENCY CONVERTER (API-Based)");
        System.out.println("============================================");
        System.out.println("  ✓ 150+ currencies supported");
        System.out.println("  ✓ Real-time exchange rates");
        System.out.println("  ✓ Cached for performance");
        System.out.println("  ✓ Multiple API fallback");
        System.out.println("============================================\n");
        
        boolean continueUsing = true;
        
        while (continueUsing) {
            System.out.println("\nSelect operation:");
            System.out.println("1. Single conversion");
            System.out.println("2. Batch conversion (to multiple currencies)");
            System.out.println("3. View exchange rate info");
            System.out.println("4. List all available currencies");
            System.out.println("5. View popular currencies");
            System.out.println("6. Exit");
            System.out.print("\nYour choice (1-6): ");
            
            int choice = 0;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number 1-6.");
                continue;
            }
            
            if (choice == 6) {
                break;
            }
            
            try {
                switch (choice) {
                    case 1:
                        performSingleConversion(scanner);
                        break;
                        
                    case 2:
                        performBatchConversion(scanner);
                        break;
                        
                    case 3:
                        viewExchangeRateInfo(scanner);
                        break;
                        
                    case 4:
                        listAllCurrencies();
                        break;
                        
                    case 5:
                        displayPopularCurrencies();
                        break;
                        
                    default:
                        System.out.println("Invalid choice. Please select 1-6.");
                }
                
            } catch (Exception e) {
                System.out.println("\n❌ Error: " + e.getMessage());
            }
            
            System.out.print("\nContinue using the converter? (yes/no): ");
            String response = scanner.nextLine().trim().toLowerCase();
            continueUsing = response.equals("yes") || response.equals("y");
        }
        
        System.out.println("\nThank you for using Advanced Currency Converter!");
        scanner.close();
    }
    
    private static void performSingleConversion(Scanner scanner) throws Exception {
        displayPopularCurrencies();
        
        System.out.print("\nEnter source currency (e.g., USD): ");
        String fromCurrency = scanner.nextLine().toUpperCase().trim();
        
        System.out.print("Enter target currency (e.g., EUR): ");
        String toCurrency = scanner.nextLine().toUpperCase().trim();
        
        System.out.print("Enter amount: ");
        double amount = Double.parseDouble(scanner.nextLine());
        
        System.out.println("\nFetching exchange rates...");
        double result = convertCurrency(amount, fromCurrency, toCurrency);
        double rate = result / amount;
        
        System.out.println("\n============================================");
        System.out.printf("%.2f %s = %.2f %s%n", amount, fromCurrency, result, toCurrency);
        System.out.printf("Exchange Rate: 1 %s = %.6f %s%n", fromCurrency, rate, toCurrency);
        System.out.println("============================================");
    }
    
    private static void performBatchConversion(Scanner scanner) throws Exception {
        displayPopularCurrencies();
        
        System.out.print("\nEnter source currency (e.g., USD): ");
        String fromCurrency = scanner.nextLine().toUpperCase().trim();
        
        System.out.print("Enter amount: ");
        double amount = Double.parseDouble(scanner.nextLine());
        
        System.out.print("Enter target currencies separated by commas (e.g., EUR,GBP,JPY): ");
        String[] toCurrencies = scanner.nextLine().toUpperCase().split(",");
        
        for (int i = 0; i < toCurrencies.length; i++) {
            toCurrencies[i] = toCurrencies[i].trim();
        }
        
        System.out.println("\nFetching exchange rates...");
        batchConvert(amount, fromCurrency, toCurrencies);
    }
    
    private static void viewExchangeRateInfo(Scanner scanner) throws Exception {
        displayPopularCurrencies();
        
        System.out.print("\nEnter first currency (e.g., USD): ");
        String currency1 = scanner.nextLine().toUpperCase().trim();
        
        System.out.print("Enter second currency (e.g., EUR): ");
        String currency2 = scanner.nextLine().toUpperCase().trim();
        
        System.out.println("\nFetching exchange rates...");
        displayExchangeRate(currency1, currency2);
    }
    
    private static void listAllCurrencies() throws Exception {
        System.out.println("\nFetching all available currencies...");
        List<String> currencies = getAllCurrencies();
        
        System.out.println("\n===== All Available Currencies =====");
        System.out.println("(" + currencies.size() + " currencies)");
        System.out.println("====================================");
        
        int count = 0;
        for (String currency : currencies) {
            System.out.print(String.format("%-5s", currency));
            count++;
            if (count % 10 == 0) {
                System.out.println();
            }
        }
        System.out.println("\n====================================");
    }
}