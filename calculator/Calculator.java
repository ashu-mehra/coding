import java.util.Scanner;
import java.util.Stack;
import java.util.HashMap;
import java.util.HashSet;

public class Calculator {
	static class PriorityMap {
		static HashMap<String, Integer> map = new HashMap();
		
		static {
			map.put("+", 1);
			map.put("-", 1);
			map.put("*", 2);
			map.put("/", 2);
		}

		static int getPriority(String operator) {
			return map.get(operator);
		}
	}

	static class OperatorInfo {
		static HashSet<Character> operatorSet = new HashSet();
		static {
			operatorSet.add('+');
			operatorSet.add('-');
			operatorSet.add('*');
			operatorSet.add('/');
		}

		static boolean isOperator(char ch) {
			return operatorSet.contains(ch);
		}
	}

	private Stack<Operand> operandStack;
	private Stack<Operator> operatorStack;
	private String initialExpression;
	int currentIndex;

	public Calculator() {
		operandStack = new Stack();
		operatorStack = new Stack();
		initialExpression = null;
		currentIndex = -1;
	}

	public double evaluateExpressionOnStack() {
		double second = operandStack.pop().getValue();
		//System.out.println("Popped " + second);
		Operator operator = operatorStack.pop();
		//System.out.println("Popped " + operator.getValue());
		double first = operandStack.pop().getValue();
		//System.out.println("Popped " + first);
		return evaluate(operator, first, second);
	}

	public Element getNextElement() {
		if (currentIndex == initialExpression.length()) {
			return null;
		}
		int prevIndex = currentIndex;
		char currentChar = initialExpression.charAt(currentIndex);
		if (OperatorInfo.isOperator(currentChar)) {
			currentIndex += 1;
			return new Operator(initialExpression.substring(prevIndex, currentIndex));
		} else {
			while (currentChar >= '0' && currentChar <= '9') {
				currentIndex += 1;
				if (currentIndex >= initialExpression.length()) {
					break;
				}
				currentChar = initialExpression.charAt(currentIndex);
			}
			return new Operand(Double.parseDouble(initialExpression.substring(prevIndex, currentIndex)));
		}

	}

	public double calculate(String expression) {
		double result = 0;
		if (expression.equals("")) {
			return 0;
		}
		initialExpression = expression;
		currentIndex = 0;

		Element element = getNextElement();
		while (element != null) {
			if (element.getType() == Element.Type.OPERAND) {
				//System.out.println("Pushing " + ((Operand)element).getValue());
				operandStack.push((Operand)element);
			} else {
				/* found an operator */
				while (!operatorStack.isEmpty() 
					&& PriorityMap.getPriority(((Operator)element).getValue()) <= PriorityMap.getPriority(operatorStack.peek().getValue())
				) {
					result = evaluateExpressionOnStack();
					Operand operand = new Operand(result);
					//System.out.println("Pushing " + operand.getValue()); 
					operandStack.push(operand);
				}
				//System.out.println("Pushing " + ((Operator)element).getValue());
				operatorStack.push((Operator)element);
			}
			element = getNextElement();
		}

		while (!operandStack.isEmpty()) {
			result = evaluateExpressionOnStack();
			if (!operandStack.isEmpty()) {
				Operand operand = new Operand(result);
				//System.out.println("Pushing " + operand.getValue()); 
				operandStack.push(operand);
			}
		}

		if (!operandStack.isEmpty() || !operatorStack.isEmpty()) {
			//System.out.println("Error: Expected stacks to be empty");
		}

		return result;
	}

	public double evaluate(Operator operator, double first, double second) {
		switch (operator.getValue()) {
			case "+":
				return first + second;
			case "-":
				return first - second;
			case "*":
				return first * second;
			case "/":
				return first / second;
		}
		return 0;
	}

	public static void main(String args[]) {
		Calculator calc = new Calculator();
		do {
			System.out.print("Enter your expression: ");
			Scanner scanner = new Scanner(System. in);
			String expr = scanner.nextLine();
			if (expr.equals("quit")) {
				System.out.println("Goodbye!");
				return;
			}
			double result = calc.calculate(expr);
			System.out.println("Ans: " + result);
		} while (true);
	}
}

abstract class Element {
	enum Type {
		OPERAND,
		OPERATOR
	}

	protected Type type;

	public Type getType() {
		return type;
	}
}

class Operand extends Element {
	private double value;

	public Operand(double v) {
		type = Element.Type.OPERAND;
		value = v;
	}

	public double getValue() {
		return value;
	}
}

class Operator extends Element {
	private String value;

	public Operator(String v) {
		type = Element.Type.OPERATOR;
		value = v;
	}

	public String getValue() {
		return value;
	}
}
