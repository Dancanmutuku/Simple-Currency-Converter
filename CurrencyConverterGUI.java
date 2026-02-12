import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class CurrencyConverterGUI extends JFrame {
    // Exchange rates relative to USD
    private static final Map<String, Double> exchangeRates = new HashMap<>();
    
    // GUI Components
    private JComboBox<String> fromCurrencyCombo;
    private JComboBox<String> toCurrencyCombo;
    private JTextField amountField;
    private JLabel resultLabel;
    private JButton convertButton;
    private JButton swapButton;
    
    static {
        // Initialize exchange rates
        exchangeRates.put("USD - US Dollar", 1.0);
        exchangeRates.put("EUR - Euro", 0.92);
        exchangeRates.put("GBP - British Pound", 0.79);
        exchangeRates.put("JPY - Japanese Yen", 149.50);
        exchangeRates.put("CNY - Chinese Yuan", 7.24);
        exchangeRates.put("INR - Indian Rupee", 83.12);
        exchangeRates.put("CAD - Canadian Dollar", 1.36);
        exchangeRates.put("AUD - Australian Dollar", 1.53);
        exchangeRates.put("CHF - Swiss Franc", 0.88);
        exchangeRates.put("MXN - Mexican Peso", 17.15);
    }
    
    public CurrencyConverterGUI() {
        // Set up the frame
        setTitle("Currency Converter");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Create main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 248, 255));
        
        // Title
        JLabel titleLabel = new JLabel("Currency Converter");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(25, 25, 112));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Amount Panel
        JPanel amountPanel = new JPanel();
        amountPanel.setBackground(new Color(240, 248, 255));
        amountPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        amountField = new JTextField(15);
        amountField.setFont(new Font("Arial", Font.PLAIN, 14));
        amountPanel.add(amountLabel);
        amountPanel.add(amountField);
        mainPanel.add(amountPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // From Currency Panel
        JPanel fromPanel = new JPanel();
        fromPanel.setBackground(new Color(240, 248, 255));
        fromPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel fromLabel = new JLabel("From:");
        fromLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        String[] currencies = exchangeRates.keySet().toArray(new String[0]);
        fromCurrencyCombo = new JComboBox<>(currencies);
        fromCurrencyCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        fromCurrencyCombo.setPreferredSize(new Dimension(250, 30));
        fromPanel.add(fromLabel);
        fromPanel.add(fromCurrencyCombo);
        mainPanel.add(fromPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Swap Button Panel
        JPanel swapPanel = new JPanel();
        swapPanel.setBackground(new Color(240, 248, 255));
        swapButton = new JButton("⇅ Swap");
        swapButton.setFont(new Font("Arial", Font.BOLD, 14));
        swapButton.setBackground(new Color(70, 130, 180));
        swapButton.setForeground(Color.WHITE);
        swapButton.setFocusPainted(false);
        swapButton.addActionListener(new SwapButtonListener());
        swapPanel.add(swapButton);
        mainPanel.add(swapPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // To Currency Panel
        JPanel toPanel = new JPanel();
        toPanel.setBackground(new Color(240, 248, 255));
        toPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel toLabel = new JLabel("To:");
        toLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        toCurrencyCombo = new JComboBox<>(currencies);
        toCurrencyCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        toCurrencyCombo.setPreferredSize(new Dimension(250, 30));
        toCurrencyCombo.setSelectedIndex(1); // Default to second currency
        toPanel.add(toLabel);
        toPanel.add(toCurrencyCombo);
        mainPanel.add(toPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Convert Button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 248, 255));
        convertButton = new JButton("Convert");
        convertButton.setFont(new Font("Arial", Font.BOLD, 16));
        convertButton.setPreferredSize(new Dimension(150, 40));
        convertButton.setBackground(new Color(34, 139, 34));
        convertButton.setForeground(Color.WHITE);
        convertButton.setFocusPainted(false);
        convertButton.addActionListener(new ConvertButtonListener());
        buttonPanel.add(convertButton);
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Result Label
        resultLabel = new JLabel("Result will appear here");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 18));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultLabel.setForeground(new Color(0, 100, 0));
        mainPanel.add(resultLabel);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Add Enter key listener to amount field
        amountField.addActionListener(new ConvertButtonListener());
    }
    
    /**
     * Converts currency amount
     */
    private double convert(double amount, String from, String to) {
        String fromCode = from.split(" - ")[0];
        String toCode = to.split(" - ")[0];
        
        double fromRate = exchangeRates.get(from);
        double toRate = exchangeRates.get(to);
        
        double amountInUSD = amount / fromRate;
        return amountInUSD * toRate;
    }
    
    /**
     * Action listener for Convert button
     */
    private class ConvertButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String amountText = amountField.getText().trim();
                
                if (amountText.isEmpty()) {
                    JOptionPane.showMessageDialog(
                        CurrencyConverterGUI.this,
                        "Please enter an amount to convert.",
                        "Input Error",
                        JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
                
                double amount = Double.parseDouble(amountText);
                
                if (amount < 0) {
                    JOptionPane.showMessageDialog(
                        CurrencyConverterGUI.this,
                        "Please enter a positive amount.",
                        "Input Error",
                        JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
                
                String fromCurrency = (String) fromCurrencyCombo.getSelectedItem();
                String toCurrency = (String) toCurrencyCombo.getSelectedItem();
                
                double result = convert(amount, fromCurrency, toCurrency);
                
                String fromCode = fromCurrency.split(" - ")[0];
                String toCode = toCurrency.split(" - ")[0];
                
                resultLabel.setText(String.format("%.2f %s = %.2f %s", 
                    amount, fromCode, result, toCode));
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    CurrencyConverterGUI.this,
                    "Please enter a valid number.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    /**
     * Action listener for Swap button
     */
    private class SwapButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int fromIndex = fromCurrencyCombo.getSelectedIndex();
            int toIndex = toCurrencyCombo.getSelectedIndex();
            
            fromCurrencyCombo.setSelectedIndex(toIndex);
            toCurrencyCombo.setSelectedIndex(fromIndex);
        }
    }
    
    public static void main(String[] args) {
        // Use SwingUtilities to ensure thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                CurrencyConverterGUI converter = new CurrencyConverterGUI();
                converter.setVisible(true);
            }
        });
    }
}