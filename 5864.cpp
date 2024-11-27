#include <iostream>
#include <cstring>
using namespace std;

int Priority(char op) {
    if (op == '+' || op == '-') return 1;
    if (op == '*' || op == '/') return 2;
    return 0;
}

void InfixToPrefix(const char infix[]) {
    char Postfix[20] = {};
    char symbol_temp[10] = {};
    int symbol_top = -1, postfix_top = -1;

    int len = strlen(infix);

    for (int i = len - 1; i >= 0; i--) {
        if (infix[i] == ')') {
            symbol_temp[++symbol_top] = infix[i];
        } else if (infix[i] == '+' || infix[i] == '-' || infix[i] == '*' || infix[i] == '/') {
            while (symbol_top >= 0 && Priority(infix[i]) < Priority(symbol_temp[symbol_top])) {
                Postfix[++postfix_top] = symbol_temp[symbol_top--];
            }
            symbol_temp[++symbol_top] = infix[i];
        } else if (infix[i] == '(') {
            while (symbol_top >= 0 && symbol_temp[symbol_top] != ')') {
                Postfix[++postfix_top] = symbol_temp[symbol_top--];
            }
            symbol_top--;
        } else {
            Postfix[++postfix_top] = infix[i];
        }
    }

    while (symbol_top >= 0) {
        Postfix[++postfix_top] = symbol_temp[symbol_top--];
    }

    for (int i = postfix_top; i >= 0; i--) {
        cout << Postfix[i];
    }
}

int main() {
    char infix[20];
    cin >> infix;
    InfixToPrefix(infix);
}
