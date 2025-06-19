import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

public class ScientificCalculator extends JFrame implements ActionListener {
    private final JTextField display;
    private final JTextField calcDisplay;
    private String operation = "";
    private String pendingUnary = "";
    private String pendingUnaryInput = "";
    private boolean startNewNumber = true;
    private boolean unaryFirst = false;
    private final DecimalFormat df = new DecimalFormat("0.########");

    public ScientificCalculator() {
        setTitle("Scientific Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 550);
        setLocationRelativeTo(null);

        calcDisplay = new JTextField("");
        calcDisplay.setEditable(false);
        calcDisplay.setFont(new Font("Arial", Font.PLAIN, 18));
        calcDisplay.setHorizontalAlignment(SwingConstants.RIGHT);
        calcDisplay.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        calcDisplay.setBackground(new Color(170, 170, 170));

        display = new JTextField("0");
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.BOLD, 28));
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setBackground(new Color(170, 170, 170));

        JPanel buttonPanel = new JPanel(new GridLayout(7, 4, 5, 5));
        buttonPanel.setBackground(new Color(64, 81, 78));

        String[] buttons = {
                "C", "←", "(", ")", "sin",
                "cos", "tan", "√", "log", "ln",
                "exp", "1/x", "x^2", "x^y", "+/-",
                "9", "7", "8", "π", ".",
                "4", "5", "6", "+", "-",
                "1", "2", "3", "*", "/",
                "0", "="
        };

        for (String text : buttons) {
            JButton btn = new JButton(text);
            btn.setFont(new Font("Arial", Font.PLAIN, 20));
            btn.setBackground(new Color(48, 227, 202));
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            btn.setForeground(Color.BLACK);
            btn.addActionListener(this);
            buttonPanel.add(btn);
        }

        setLayout(new BorderLayout(5, 5));
        JPanel displayPanel = new JPanel(new GridLayout(2, 1));
        displayPanel.add(calcDisplay);
        displayPanel.add(display);
        add(displayPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        display.setText("");
        calcDisplay.setText("0");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        try {
            if (isNumberOrDot(command)) {
                handleNumberOrDot(command);
            } else if (isUnaryOperator(command)) {
                handleUnaryOperator(command);
            } else if (command.equals("π")) {
                handlePi();
            } else if (command.equals("+/-")) {
                handleNegate();
            } else if (command.equals("C")) {
                clearAll();
            } else if (command.equals("←")) {
                handleBackspace();
            } else if (isBinaryOperator(command)) {
                handleBinaryOperator(command);
            } else if (command.equals("=")) {
                handleEquals();
            } else if (command.equals("(") || command.equals(")")) {
                Toolkit.getDefaultToolkit().beep();
            }
        } catch (NumberFormatException ex) {
            display.setText("Error");
            clearOps();
        }
    }

    private boolean isNumberOrDot(String command) {
        return command.matches("[0-9]") || command.equals(".");
    }

    private void handleNumberOrDot(String command) {
        if (startNewNumber) {
            if (pendingUnary.length() > 0 && unaryFirst) {
                pendingUnaryInput = command.equals(".") ? "0." : command;
                calcDisplay.setText(pendingUnary + "(" + pendingUnaryInput + ")");
                display.setText("");
            } else {
                String current = calcDisplay.getText();
                if (!current.isEmpty() && isOperator(current.charAt(current.length() - 1))) {
                    calcDisplay.setText(current + (command.equals(".") ? "0." : command));
                } else {
                    calcDisplay.setText(command.equals(".") ? "0." : command);
                }
                display.setText("");
            }
            startNewNumber = false;
        } else {
            if (!(command.equals(".")
                    && ((pendingUnary.length() > 0 && unaryFirst ? pendingUnaryInput : calcDisplay.getText())
                            .contains(".")))) {
                if (pendingUnary.length() > 0 && unaryFirst) {
                    pendingUnaryInput = pendingUnaryInput.equals("0") && !command.equals(".") ? command
                            : pendingUnaryInput + command;
                    calcDisplay.setText(pendingUnary + "(" + pendingUnaryInput + ")");
                    display.setText("");
                } else {
                    calcDisplay.setText(calcDisplay.getText().equals("0") && !command.equals(".") ? command
                            : calcDisplay.getText() + command);
                    display.setText("");
                }
            }
        }
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    private void handleUnaryOperator(String command) {
        if (command.equals("x^2")) {
            String current = calcDisplay.getText();
            if (current.isEmpty())
                current = "0";
            calcDisplay.setText(current + "^2");
            display.setText("");
            startNewNumber = true;
            pendingUnary = "";
            unaryFirst = false;
            pendingUnaryInput = "";
        } else if (command.equals("1/x")) {
            String current = calcDisplay.getText();
            if (current.isEmpty())
                current = "0";
            calcDisplay.setText("1/(" + current + ")");
            display.setText("");
            startNewNumber = true;
            pendingUnary = "1/x";
            unaryFirst = true;
            pendingUnaryInput = current;
        } else {
            calcDisplay.setText(command + "()");
            pendingUnary = command;
            unaryFirst = true;
            pendingUnaryInput = "";
            display.setText("");
            startNewNumber = true;
        }
    }

    private void handlePi() {
        String current = calcDisplay.getText();
        if (current == null || current.isEmpty() || current.equals("0")) {
            calcDisplay.setText("π");
        } else {
            char last = current.charAt(current.length() - 1);
            if (isOperator(last)) {
                calcDisplay.setText(current + "π");
            } else if (Character.isDigit(last) || last == '.') {
                calcDisplay.setText(current + "π");
            } else {
                calcDisplay.setText(current + "π");
            }
        }
        display.setText("");
        startNewNumber = false;
    }

    private void handleNegate() {
        String current = calcDisplay.getText();
        if (current == null || current.isEmpty() || current.equals("0")) {
            calcDisplay.setText("-");
            display.setText("");
            startNewNumber = false;
            return;
        }
        if (current.startsWith("-")) {
            calcDisplay.setText(current.substring(1));
        } else {
            calcDisplay.setText("-" + current);
        }
        display.setText("");
        startNewNumber = false;
    }

    private void clearAll() {
        display.setText("");
        calcDisplay.setText("0");
        operation = "";
        pendingUnary = "";
        pendingUnaryInput = "";
        unaryFirst = false;
        startNewNumber = true;
    }

    private void handleBackspace() {
        String current = calcDisplay.getText();
        if (current != null && current.length() > 0) {
            calcDisplay.setText(current.substring(0, current.length() - 1));
        }
    }

    private void handleBinaryOperator(String command) {
        if (pendingUnary.length() > 0 && unaryFirst) {
            double value = Double.parseDouble(calcDisplay.getText().replaceAll("[^0-9.]", ""));
            applyUnary(pendingUnary, value);
            if (command.equals("x^y")) {
                calcDisplay.setText(df.format(value) + "^");
            } else {
                calcDisplay.setText(pendingUnary + "(" + df.format(value) + ")" + command);
            }
            pendingUnary = "";
            unaryFirst = false;
            operation = command;
            startNewNumber = true;
        } else {
            String current = calcDisplay.getText();
            if (command.equals("x^y")) {
                calcDisplay.setText(current + "^");
            } else {
                calcDisplay.setText(current + command);
            }
            operation = command;
            startNewNumber = true;
        }
        display.setText("");
    }

    private void handleEquals() {
        String expr = calcDisplay.getText();
        if (expr.isEmpty()) {
            display.setText("");
            return;
        }
        double resultValue = 0;
        try {
            if (expr.matches("[0-9.]+π")) {
                String num = expr.replace("π", "");
                resultValue = Double.parseDouble(num) * Math.PI;
                display.setText(df.format(resultValue));
                calcDisplay.setText(expr);
                startNewNumber = true;
                return;
            }
            String evalExpr = expr.replaceAll("([0-9.]+)π", "$1*" + Math.PI)
                    .replace("π", String.valueOf(Math.PI));
            if (evalExpr.endsWith("^2")) {
                String base = evalExpr.substring(0, evalExpr.length() - 2);
                double value = Double.parseDouble(base);
                resultValue = value * value;
                display.setText(df.format(resultValue));
                calcDisplay.setText(expr);
                startNewNumber = true;
                return;
            }
            if (evalExpr.contains("^")) {
                String[] parts = evalExpr.split("\\^");
                if (parts.length == 2) {
                    double left = Double.parseDouble(parts[0]);
                    double right = Double.parseDouble(parts[1]);
                    resultValue = Math.pow(left, right);
                    display.setText(df.format(resultValue));
                    calcDisplay.setText(expr);
                    startNewNumber = true;
                    operation = "";
                    return;
                }
            }
            if (evalExpr.startsWith("1/(") && evalExpr.endsWith(")")) {
                String inside = evalExpr.substring(3, evalExpr.length() - 1);
                double value = Double.parseDouble(inside);
                if (value == 0) {
                    display.setText("Error");
                } else {
                    resultValue = 1.0 / value;
                    display.setText(df.format(resultValue));
                }
                calcDisplay.setText(expr);
                startNewNumber = true;
                pendingUnary = "";
                unaryFirst = false;
                pendingUnaryInput = "";
                return;
            }
            if (pendingUnary.length() > 0 && unaryFirst) {
                double value = pendingUnaryInput.isEmpty() ? 0
                        : Double.parseDouble(pendingUnaryInput.replaceAll("([0-9.]+)π", "$1*" + Math.PI).replace("π",
                                String.valueOf(Math.PI)));
                if (pendingUnary.equals("tan") && (Math.abs((value % 180) - 90) < 1e-9)) {
                    display.setText("∞");
                } else {
                    resultValue = applyUnary(pendingUnary, value);
                    display.setText(df.format(resultValue));
                }
                calcDisplay.setText(pendingUnary + "(" + df.format(value) + ")");
                pendingUnary = "";
                unaryFirst = false;
                pendingUnaryInput = "";
                startNewNumber = true;
            } else if (!operation.isEmpty()) {
                String[] parts = evalExpr.split(java.util.regex.Pattern.quote(operation));
                if (parts.length == 2) {
                    double left = Double.parseDouble(parts[0]);
                    double right = Double.parseDouble(parts[1]);
                    switch (operation) {
                        case "+":
                            resultValue = left + right;
                            break;
                        case "-":
                            resultValue = left - right;
                            break;
                        case "*":
                            resultValue = left * right;
                            break;
                        case "/":
                            if (right == 0) {
                                display.setText("Error");
                                calcDisplay.setText("");
                                clearOps();
                                return;
                            }
                            resultValue = left / right;
                            break;
                    }
                    calcDisplay.setText(expr);
                } else {
                    resultValue = Double.parseDouble(evalExpr);
                }
                display.setText(df.format(resultValue));
            } else {
                resultValue = Double.parseDouble(evalExpr);
                display.setText(df.format(resultValue));
            }
            operation = "";
        } catch (Exception ex) {
            display.setText("Error");
            calcDisplay.setText("");
            clearOps();
        }
        startNewNumber = true;
    }

    private void clearOps() {
        operation = "";
        pendingUnary = "";
        pendingUnaryInput = "";
        unaryFirst = false;
    }

    private double applyUnary(String unaryOp, double value) {
        switch (unaryOp) {
            case "sin":
                return Math.sin(Math.toRadians(value));
            case "cos":
                return Math.cos(Math.toRadians(value));
            case "tan":
                return Math.tan(Math.toRadians(value));
            case "sqrt":
            case "√":
                return Math.sqrt(value);
            case "log":
                return Math.log10(value);
            case "ln":
                return Math.log(value);
            case "exp":
                return Math.exp(value);
            case "1/x":
                return 1.0 / value;
            default:
                return value;
        }
    }

    private boolean isUnaryOperator(String command) {
        return command.equals("sin") || command.equals("cos") || command.equals("tan") ||
                command.equals("sqrt") || command.equals("√") || command.equals("log") || command.equals("ln") ||
                command.equals("exp") || command.equals("1/x") || command.equals("x^2");
    }

    private boolean isBinaryOperator(String command) {
        return command.equals("+") || command.equals("-") || command.equals("*") ||
                command.equals("/") || command.equals("x^y");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ScientificCalculator calculator = new ScientificCalculator();
            calculator.setVisible(true);
        });
    }
}