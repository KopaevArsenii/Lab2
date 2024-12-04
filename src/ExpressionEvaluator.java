import java.util.Scanner;
import java.util.Stack;

public class ExpressionEvaluator {

    // Функция для проверки, является ли символ цифрой
    public static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    // Функция для проверки, является ли символ буквой
    public static boolean isLetter(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }

    // Функция для вычисления выражения
    public static double evaluate(String expression, java.util.Map<String, Double> variables) {
        Stack<Double> stack = new Stack<>();
        Stack<Character> ops = new Stack<>();

        // Удаляем все пробелы и символы переноса строки
        expression = expression.replaceAll("[\\s\\n\\r]+", "");

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            if (ch == ' ') continue;  // Игнорируем пробелы

            // Если символ - это цифра или целое число
            if (isDigit(ch)) {
                StringBuilder num = new StringBuilder();
                while (i < expression.length() && isDigit(expression.charAt(i))) {
                    num.append(expression.charAt(i++));
                }
                stack.push(Double.parseDouble(num.toString()));
                i--;  // Возвращаемся на предыдущую позицию, так как i уже увеличено в цикле
            }
            // Если символ - это буква, значит это переменная
            else if (isLetter(ch)) {
                StringBuilder variableName = new StringBuilder();
                while (i < expression.length() && isLetter(expression.charAt(i))) {
                    variableName.append(expression.charAt(i++));
                }
                String varName = variableName.toString();
                if (variables.containsKey(varName)) {
                    stack.push(variables.get(varName));
                } else {
                    throw new IllegalArgumentException("Неизвестная переменная: " + varName);
                }
                i--; // Возвращаемся назад, так как i уже увеличено в цикле
            }
            else if (ch == '(') {
                ops.push(ch);
            } else if (ch == ')') {
                while (!ops.isEmpty() && ops.peek() != '(') {
                    stack.push(applyOp(ops.pop(), stack.pop(), stack.pop()));
                }
                ops.pop();  // Убираем '('
            } else if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                while (!ops.isEmpty() && precedence(ops.peek()) >= precedence(ch)) {
                    stack.push(applyOp(ops.pop(), stack.pop(), stack.pop()));
                }
                ops.push(ch);
            } else {
                throw new IllegalArgumentException("Invalid character: " + ch);
            }
        }

        while (!ops.isEmpty()) {
            stack.push(applyOp(ops.pop(), stack.pop(), stack.pop()));
        }

        return stack.pop();
    }

    // Применить операцию
    public static double applyOp(char op, double b, double a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/': return a / b;
            default: throw new UnsupportedOperationException("Unsupported operation: " + op);
        }
    }

    // Определить приоритет операции
    public static int precedence(char op) {
        if (op == '+' || op == '-') return 1;
        if (op == '*' || op == '/') return 2;
        return 0;
    }

    // Главный метод
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Запрашиваем выражение
        System.out.print("Введите выражение: ");
        String expression = scanner.nextLine();

        // Создаем Map для хранения переменных
        java.util.Map<String, Double> variables = new java.util.HashMap<>();

        // Извлекаем переменные из выражения
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("[a-zA-Z]+").matcher(expression);
        while (matcher.find()) {
            String variable = matcher.group();
            if (!variables.containsKey(variable)) {
                System.out.print("Введите значение для переменной " + variable + ": ");
                double value = Double.parseDouble(scanner.nextLine());
                variables.put(variable, value);
            }
        }

        // Удаляем пробелы и символы новой строки
        expression = expression.replaceAll("[\\s\\n\\r]+", "");

        // Демонстрируем работу ExpressionEvaluator
        try {
            double result = evaluate(expression, variables);
            System.out.println("Результат: " + result);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}
