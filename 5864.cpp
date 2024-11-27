#include <iostream>
#include <stack>
#include <string>
using namespace std;

int Priority(char op) {
    switch (op) {
        case '+':
        case '-':
            return 1;
        case '*':
        case '/':
            return 2;
        default:
            return 0;
    }
}

void InfixToPrefix(const string& infix) {
    stack<char> operators;
    string result;

    for (int i = infix.length() - 1; i >= 0; --i) {
        char current = infix[i];

        if (isdigit(current) || isalpha(current)) {
            result += current;
        } else if (current == ')') {
            operators.push(current);
        } else if (current == '(') {
            while (!operators.empty() && operators.top() != ')') {
                result += operators.top();
                operators.pop();
            }
            operators.pop();
        } else {
            while (!operators.empty() && Priority(current) < Priority(operators.top())) {
                result += operators.top();
                operators.pop();
            }
            operators.push(current);
        }
    }

    while (!operators.empty()) {
        result += operators.top();
        operators.pop();
    }

    reverse(result.begin(), result.end());
    cout << result << endl;
}

int main() {
    string itemset;
    cin >> itemset;
    InfixToPrefix(itemset);
    return 0;
}
