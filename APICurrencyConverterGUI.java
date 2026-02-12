import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * GUI Currency Converter with Live API Integration
 * Supports real-time conversion between any world currencies
 */
public class APICurrencyConverterGUI extends JFrame {
    
    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/";
    
    // GUI Components
    private JComboBox<String> fromCurrencyCombo;
    private JComboBox<String> toCurrencyCombo;
    private JTextField amountField;
    private JLabel resultLabel;
    private JLabel rateLabel;
    private JButton convertButton;
    private JButton swapButton;
    private JButton refreshButton;
    private List<String> currencyList;
    
    public APICurrencyConverterGUI() {
        // Initialize currency list
        currencyList = new ArrayList<>();
        loadCurrencyList();
        
        // Set up the frame
        setTitle("Live Currency Converter");
        setSize(550, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 250));
        
        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(245, 245, 250));
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel("Live Currency Converter");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(25, 25, 112));
        
        JLabel subtitleLabel = new JLabel("Real-time exchange rates");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setForeground(new Color(100, 100, 100));
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);
        
        mainPanel.add(titlePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Amount Panel
        JPanel amountPanel = createLabeledPanel("Amount:");
        amountField = new JTextField(15);
        amountField.setFont(new Font("Arial", Font.PLAIN, 16));
        amountField.setPreferredSize(new Dimension(250, 35));
        amountPanel.add(amountField);
        mainPanel.add(amountPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // From Currency Panel
        JPanel fromPanel = createLabeledPanel("From:");
        String[] currencies = currencyList.toArray(new String[0]);
        fromCurrencyCombo = new JComboBox<>(currencies);
        fromCurrencyCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        fromCurrencyCombo.setPreferredSize(new Dimension(250, 35));
        fromCurrencyCombo.setSelectedItem("USD");
        fromPanel.add(fromCurrencyCombo);
        mainPanel.add(fromPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Swap Button Panel
        JPanel swapPanel = new JPanel();
        swapPanel.setBackground(new Color(245, 245, 250));
        swapButton = new JButton("⇅ Swap Currencies");
        swapButton.setFont(new Font("Arial", Font.BOLD, 14));
        swapButton.setBackground(new Color(70, 130, 180));
        swapButton.setForeground(Color.WHITE);
        swapButton.setFocusPainted(false);
        swapButton.setPreferredSize(new Dimension(180, 35));
        swapButton.addActionListener(e -> swapCurrencies());
        swapPanel.add(swapButton);
        mainPanel.add(swapPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // To Currency Panel
        JPanel toPanel = createLabeledPanel("To:");
        toCurrencyCombo = new JComboBox<>(currencies);
        toCurrencyCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        toCurrencyCombo.setPreferredSize(new Dimension(250, 35));
        toCurrencyCombo.setSelectedItem("EUR");
        toPanel.add(toCurrencyCombo);
        mainPanel.add(toPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 245, 250));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        convertButton = new JButton("Convert");
        convertButton.setFont(new Font("Arial", Font.BOLD, 16));
        convertButton.setPreferredSize(new Dimension(150, 45));
        convertButton.setBackground(new Color(34, 139, 34));
        convertButton.setForeground(Color.WHITE);
        convertButton.setFocusPainted(false);
        convertButton.addActionListener(e -> performConversion());
        
        refreshButton = new JButton("↻ Refresh");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 14));
        refreshButton.setPreferredSize(new Dimension(120, 45));
        refreshButton.setBackground(new Color(100, 149, 237));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> loadCurrencyList());
        
        buttonPanel.add(convertButton);
        buttonPanel.add(refreshButton);
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Result Panel
        JPanel resultPanel = new JPanel();
        resultPanel.setBackground(new Color(245, 245, 250));
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        
        resultLabel = new JLabel("Enter amount and click Convert");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 20));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultLabel.setForeground(new Color(0, 100, 0));
        
        rateLabel = new JLabel(" ");
        rateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        rateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rateLabel.setForeground(new Color(100, 100, 100));
        
        resultPanel.add(resultLabel);
        resultPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        resultPanel.add(rateLabel);
        
        mainPanel.add(resultPanel);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Add Enter key listener
        amountField.addActionListener(e -> performConversion());
    }
    
    /**
     * Creates a labeled panel with consistent styling
     */
    private JPanel createLabeledPanel(String labelText) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 245, 250));
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setPreferredSize(new Dimension(80, 30));
        
        panel.add(label);
        return panel;
    }
    
    /**
     * Loads available currencies from API
     */
    private void loadCurrencyList() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    JSONObject data = getExchangeRates("USD");
                    JSONObject rates = data.getJSONObject("rates");
                    
                    currencyList.clear();
                    currencyList.add("USD");
                    
                    for (String key : rates.keySet()) {
                        currencyList.add(key);
                    }
                    
                    Collections.sort(currencyList);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            
            @Override
            protected void done() {
                if (fromCurrencyCombo != null && !currencyList.isEmpty()) {
                    updateCombos();
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Updates currency combo boxes
     */
    private void updateCombos() {
        String selectedFrom = (String) fromCurrencyCombo.getSelectedItem();
        String selectedTo = (String) toCurrencyCombo.getSelectedItem();
        
        fromCurrencyCombo.removeAllItems();
        toCurrencyCombo.removeAllItems();
        
        for (String currency : currencyList) {
            fromCurrencyCombo.addItem(currency);
            toCurrencyCombo.addItem(currency);
        }
        
        if (currencyList.contains(selectedFrom)) {
            fromCurrencyCombo.setSelectedItem(selectedFrom);
        }
        if (currencyList.contains(selectedTo)) {
            toCurrencyCombo.setSelectedItem(selectedTo);
        }
    }
    
    /**
     * Fetches exchange rates from API
     */
    private JSONObject getExchangeRates(String baseCurrency) throws Exception {
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
            throw new Exception("API request failed");
        }
    }
    
    /**
     * Performs currency conversion
     */
    private void performConversion() {
        SwingWorker<String[], Void> worker = new SwingWorker<String[], Void>() {
            @Override
            protected String[] doInBackground() throws Exception {
                String amountText = amountField.getText().trim();
                
                if (amountText.isEmpty()) {
                    return new String[]{"ERROR", "Please enter an amount"};
                }
                
                double amount = Double.parseDouble(amountText);
                
                if (amount < 0) {
                    return new String[]{"ERROR", "Please enter a positive amount"};
                }
                
                String fromCurrency = (String) fromCurrencyCombo.getSelectedItem();
                String toCurrency = (String) toCurrencyCombo.getSelectedItem();
                
                if (fromCurrency.equals(toCurrency)) {
                    return new String[]{"RESULT", 
                        String.format("%.2f %s = %.2f %s", amount, fromCurrency, amount, toCurrency),
                        "Exchange Rate: 1.000000"};
                }
                
                JSONObject data = getExchangeRates(fromCurrency);
                JSONObject rates = data.getJSONObject("rates");
                double exchangeRate = rates.getDouble(toCurrency);
                double result = amount * exchangeRate;
                
                return new String[]{"RESULT",
                    String.format("%.2f %s = %.2f %s", amount, fromCurrency, result, toCurrency),
                    String.format("Exchange Rate: 1 %s = %.6f %s", fromCurrency, exchangeRate, toCurrency)};
            }
            
            @Override
            protected void done() {
                try {
                    String[] result = get();
                    
                    if (result[0].equals("ERROR")) {
                        JOptionPane.showMessageDialog(
                            APICurrencyConverterGUI.this,
                            result[1],
                            "Input Error",
                            JOptionPane.WARNING_MESSAGE
                        );
                    } else {
                        resultLabel.setText(result[1]);
                        rateLabel.setText(result[2]);
                    }
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                        APICurrencyConverterGUI.this,
                        "Error: " + e.getMessage() + 
                        "\nPlease check your internet connection.",
                        "Conversion Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        
        resultLabel.setText("Converting...");
        rateLabel.setText("Fetching live rates...");
        worker.execute();
    }
    
    /**
     * Swaps the from and to currencies
     */
    private void swapCurrencies() {
        int fromIndex = fromCurrencyCombo.getSelectedIndex();
        int toIndex = toCurrencyCombo.getSelectedIndex();
        
        fromCurrencyCombo.setSelectedIndex(toIndex);
        toCurrencyCombo.setSelectedIndex(fromIndex);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            APICurrencyConverterGUI converter = new APICurrencyConverterGUI();
            converter.setVisible(true);
        });
    }
}