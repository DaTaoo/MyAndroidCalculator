package com.example.todd.calculator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CalculatorFragment extends Fragment {

    private static final String TAG = "CalculatorFragment";

    private static final String FLAG_DEL = "flag_del";


    private StringStack postfixStack = new StringStack();
    private StringStack calculateStack = new StringStack();

    private ArrayList<String> infixExpression = new ArrayList<>();//用于记录公式中的操作数和操作符


    private String lastInput = "";//记录上一个输入
    private String nextInput; //存储本次输入

    private Button[] mButtons = new Button[17];
    private TextView mResultText;
    private TextView mExpressionText;

    public static Fragment newInstance() {
        return new CalculatorFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_caculator, container, false);

        mResultText = view.findViewById(R.id.textview_result);
        mExpressionText = view.findViewById(R.id.textview_expression);

        //like an idiot
        mButtons[0] = view.findViewById(R.id.button_num_7);
        mButtons[1] = view.findViewById(R.id.button_num_8);
        mButtons[2] = view.findViewById(R.id.button_num_9);
        mButtons[3] = view.findViewById(R.id.button_num_4);
        mButtons[4] = view.findViewById(R.id.button_num_5);
        mButtons[5] = view.findViewById(R.id.button_num_6);
        mButtons[6] = view.findViewById(R.id.button_num_1);
        mButtons[7] = view.findViewById(R.id.button_num_2);
        mButtons[8] = view.findViewById(R.id.button_num_3);
        mButtons[9] = view.findViewById(R.id.button_dot);
        mButtons[10] = view.findViewById(R.id.button_num_0);
        mButtons[11] = view.findViewById(R.id.button_op_clr);
        mButtons[12] = view.findViewById(R.id.button_op_del);
        mButtons[13] = view.findViewById(R.id.button_op_div);
        mButtons[14] = view.findViewById(R.id.button_op_mul);
        mButtons[15] = view.findViewById(R.id.button_op_subtract);
        mButtons[16] = view.findViewById(R.id.button_op_plus);

        for (Button button : mButtons) {
            button.setOnClickListener(new ButtonListener());
        }

        return view;
    }

    private class ButtonListener implements View.OnClickListener {




        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_num_1:
                    nextInput = "1";
                    break;
                case R.id.button_num_2:
                    nextInput = "2";
                    break;
                case R.id.button_num_3:
                    nextInput = "3";
                    break;
                case R.id.button_num_4:
                    nextInput = "4";
                    break;
                case R.id.button_num_5:
                    nextInput = "5";
                    break;
                case R.id.button_num_6:
                    nextInput = "6";
                    break;
                case R.id.button_num_7:
                    nextInput = "7";
                    break;
                case R.id.button_num_8:
                    nextInput = "8";
                    break;
                case R.id.button_num_9:
                    nextInput = "9";
                    break;
                case R.id.button_num_0:
                    nextInput = "0";
                    break;
                case R.id.button_dot:
                    nextInput = ".";
                    break;
                case R.id.button_op_del:
                    nextInput = FLAG_DEL;
                    break;
                case R.id.button_op_mul:
                    nextInput = "*";
                    break;
                case R.id.button_op_subtract:
                    nextInput = "-";
                    break;
                case R.id.button_op_div:
                    nextInput = "/";
                    break;
                case R.id.button_op_clr:
                    infixExpression.clear();
                    nextInput = "C";
                    break;
                case R.id.button_op_plus:
                    nextInput = "+";
                    break;
                default:
                    break;
            }

            String displayResult = infixExpBuilder(nextInput);

            String displayExpression = displayExpressionBuilder();

            mExpressionText.setText(displayExpression);

            mResultText.setText(displayResult);

        }

    }

    //中缀转后缀
    private List<String> infixToPostfix(List<String> infixExp) {
        // 输出
        ArrayList<String> postfixExp = new ArrayList<>();

        int index = 0;

        //处理特殊情况，若第一个处理的是负号（即第一个操作数是负数）
        //现在输出队列添加0再把负号入栈
        if (infixExp.get(0).equals("-")) {
            postfixExp.add("0");
        }
        // 数字直接输出，符号入栈
        while (index <= infixExp.size() - 1) {
            String nextElement = infixExp.get(index);
            if (nextElement.matches("\\d+(\\.\\d+)?")) { //正则表达式 匹配整数或小数
                postfixExp.add(nextElement);
            } else { // 不是数字，只能是运算符

                // 是否为空，为空直接入栈
                if (postfixStack.isEmpty()) {
                    postfixStack.push(nextElement);
                } else { // 不为空，判断运算符优先级： */ > +-
                    // 从栈顶开始，如果栈内有元素优先级大于等于nextElement，这些元素要出栈到输出
                    // 然后nextElement入栈

                    // 做个while循环，如果栈顶优先级大于等于nextElement则一直出栈直到优先级条件成立或者栈已为空
                    boolean canPush = false;
                    while (!canPush) {
                        String top = postfixStack.peek();
                        if (top == null) {
                            // 栈已为空，入栈
                            postfixStack.push(nextElement);
                            canPush = true;
                        } else if (nextElement.equals("*") || nextElement.equals("/")) {
                            if (top.equals("*") || top.equals("/")) {
                                // 栈顶为乘除符号，优先级相同，应出栈到输出
                                postfixExp.add(postfixStack.pop());
                            } else {
                                //// 栈顶只能为加减
                                // nextElement入栈
                                postfixStack.push(nextElement);
                                canPush = true;
                            }
                        } else {
                            // nextElement为加减
                            if (top.equals("*") || top.equals("/")) {
                                // 栈顶为乘除符号，优先级大，应出栈到输出
                                postfixExp.add(postfixStack.pop());
                            } else {
                                //// 栈顶只能为加减 优先级相同，应出栈到输出
                                postfixExp.add(postfixStack.pop());
                            }
                        }
                    }
                }
            }
            index++;
        }
        while (!postfixStack.isEmpty()) {
            postfixExp.add(postfixStack.pop());
        }
        return postfixExp;
    }

    private String calculatePostfix(List<String> postfix) {
        calculateStack.clear();

        //后缀表达式计算：不需要考虑优先级，直接从左到右进行操作
        int index = 0;




        double result;

        String output;

        while (index < postfix.size()) {
            String next = postfix.get(index);

            //如果是数字，入栈
            if (next.matches("\\d+(\\.\\d+)?")) {
                calculateStack.push(next);
            } else {
                //不是数字，取出从栈顶开始向下两个数
                String numRight = calculateStack.pop();
                String numLeft = calculateStack.pop();

                BigDecimal numR = new BigDecimal(numRight);
                BigDecimal numL = new BigDecimal(numLeft);


//                numR = Double.valueOf(numRight);
//                numL = Double.valueOf(numLeft);

                Log.i(TAG, "numR: " + numR);
                Log.i(TAG, "numL: " + numL);

                //判断运算符类型 默认为浮点数计算
                switch (next) {
                    case "+":
                        result = numL.add(numR).doubleValue();
                        break;
                    case "-":
                        result = numL.subtract(numR).doubleValue();
                        break;
                    case "*":
                        result = numL.multiply(numR).doubleValue();
                        break;
                    case "/":
                        if (numR.compareTo(BigDecimal.valueOf(0.0)) == 0) {
                            return "N/A";
                        } else {
                            result = numL.divide(numR, RoundingMode.HALF_UP).doubleValue();
                        }
                        break;
                    default:
                        result = 0.0;
                }

                boolean isResultInteger;

                Log.i(TAG, "result: " + result);

                Double resultF = result;
                //判断计算结果是否是整形
                //判断依据是检测此数与它的整数形式是否相等，相等则是整形
                if (resultF.intValue() == resultF) {
                    isResultInteger = true;
                } else {
                    isResultInteger = false;
                }

                //根据计算结果类型转换为整形或浮点型
                if (isResultInteger) {
                    int intTemp = (int) result;
                    output = String.valueOf(intTemp);
                } else {
                    output = String.valueOf(result);
                }

                //计算完后结果入栈
                calculateStack.push(output);
            }
            index++;
        }
        //如果计算完成，栈只有一个元素且为计算结果

        return calculateStack.pop();
    }

    private String infixExpBuilder(String nextInput) {

        String displayResult
                ;
        //规则:
        // 1.如果上一个输入已经是符号，则新输入覆盖上一个输入
        // 2.操作数的定义：除了第一个操作数，符号与符号之间（不包括小数点）都视为一个操作数，第一个操作数有特殊规定
        // 3.若第一个输入为+-.符号，则这个符号也视为第一个操作数的一部分
        // 4.如果是删除操作和等于号，特殊处理
        // 5.输入小数点时，如果上一个输入是数字，检查上一个操作数是否已经包含小数点，如果包含就不能再输入

        if (infixExpression.isEmpty()) {
            lastInput = "";
        }

        if (nextInput.equals(FLAG_DEL)) {
            //删除操作，如果已经没有元素则不响应
            if (!infixExpression.isEmpty()) {
                //如果还有，则判断是操作数还是运算符
                if (infixExpression.get(infixExpression.size() - 1).matches("[+\\-*/]")) {
                    //不是操作数，直接删除这一元素
                    infixExpression.remove(infixExpression.size() - 1);
                } else {
                    //是操作数
                    StringBuilder numBuilder = new StringBuilder(infixExpression.remove(infixExpression.size() - 1));
                    //删除尾数
                    numBuilder.deleteCharAt(numBuilder.length() - 1);
                    //检测是否还有字符，如果还有，继续放到表达式中
                    if (numBuilder.length() > 0) {
                        lastInput = String.valueOf(numBuilder.charAt(numBuilder.length() - 1));
                        infixExpression.add(numBuilder.toString());
                    }
                }
            }
        } else if (nextInput.matches("[+\\-*/.]")) {
            //输入运算符号+-*/.
            //判断是否第一个输入
            if (infixExpression.isEmpty()) {
                //是第一个输入，只能输入+-.
                // 其他运算符不做处理
                if (nextInput.matches("[+\\-.]")) {
                    //构建第一个操作数
                    if (nextInput.equals(".")) {
                        //输入是小数点，则自动构建小数
                        String firstNum = "0.";
                        infixExpression.add(firstNum);
                    } else {
                        //输入是加减号，加号忽略，减号构建负数
                        if (nextInput.equals("-")) {
                            infixExpression.add(nextInput);
                        }
                    }
                }
            } else {
                //不是第一个输入，而且上一个输入是符号
                if (lastInput.matches("[+\\-*/]")) {
                    //上一次输入是加减乘除
                    if (nextInput.equals(".")) {
                        //检测上个输入是否是第一个元素，如果是，那只能是负号
                        if (infixExpression.size() == 1) {
                            infixExpression.remove(infixExpression.size() - 1);
                            //输入是小数点，则自动构建小数
                            String num = "0.";
                            infixExpression.add(num);
                        } else {
                            //如果这次输入的是小数点，删除上个运算符并将小数点添加在上个操作数上
                            infixExpression.remove(infixExpression.size() - 1);
                            StringBuilder numBuilder = new StringBuilder(infixExpression.remove(infixExpression.size() - 1));
                            numBuilder.append(nextInput);
                            infixExpression.add(numBuilder.toString());
                        }
                    } else {
                        //这次输入也是运算符，如果上次输入不是第一个元素，覆盖上一个元素
                        //如果上一个元素是第一个元素，那只能是负号，忽略这步操作
                        if (infixExpression.size() > 1) {
                            infixExpression.remove(infixExpression.size() - 1);
                            infixExpression.add(nextInput);
                        }
                    }
                } else if (lastInput.matches("\\.")) {
                    //如果上一个输入是小数点，而这次也是小数点，则不处理
                    if (!nextInput.equals(".")) {
                        //只能是加减乘除，则要删除操作数的小数点并添加运算符
                        StringBuilder numBuilder = new StringBuilder(infixExpression.remove(infixExpression.size() - 1));
                        numBuilder.deleteCharAt(numBuilder.length() - 1);
                        infixExpression.add(numBuilder.toString());
                        infixExpression.add(nextInput);
                    }

                } else if (lastInput.matches("\\d")) {
                    //上一次输入是数字
                    if (nextInput.matches("\\.")) {
                        //这次输入是小数点
                        //先检查上一个操作数是否已经有小数点，有的话不处理
                        if (!infixExpression.get(infixExpression.size() - 1).contains(".")) {
                            //没有小数点
                            StringBuilder numBuilder = new StringBuilder(infixExpression.remove(infixExpression.size() - 1));
                            numBuilder.append(nextInput);
                            infixExpression.add(numBuilder.toString());
                        }
                    } else {
                        //只能是加减乘除
                        infixExpression.add(nextInput);
                    }
                }
            }
        } else if (nextInput.matches("\\d")) {
            //输入0-9
            //如果上一个输入是数字或小数点，合并到上一个数字上
            //如果是运算符，创建新的操作数
            //如果之前输入为空那就直接创建新操作数
            if (infixExpression.isEmpty()) {
                infixExpression.add(nextInput);
            } else if (lastInput.matches("[\\d.]")) {
                //数字或小数点
                StringBuilder numBuilder = new StringBuilder(infixExpression.remove(infixExpression.size() - 1));
                numBuilder.append(nextInput);
                infixExpression.add(numBuilder.toString());
            } else if (lastInput.matches("[+\\-*/]")) {
                //加减乘除
                infixExpression.add(nextInput);
            }
        }

        //更新lastInput机制：
        //不是空表达式
        //如果最后一个元素是操作数，lastInput应为最后一个元素的最后一个数字，包括未输入尾数的小数
        //如果最后一个元素是操作符，lastInput直接赋值为该操作符
        if (infixExpression.isEmpty()) {
            lastInput = "";
        } else if (infixExpression.get(infixExpression.size() - 1).matches("[+\\-*/]")) {
            //是操作符
            lastInput = infixExpression.get(infixExpression.size() - 1);
        } else {
            //是操作数
            String tempString = infixExpression.get(infixExpression.size() - 1);
            lastInput = String.valueOf(tempString.charAt(tempString.length() - 1));
        }

        //实现自动求值功能要求
        // 1.操作数至少为两个
        // 2.如果最后一个元素是加减乘除，则忽略
        // 3.如果输入的是小数点，则最后一个数未完成构建，计算时应忽略这个小数点

        if (infixExpression.isEmpty()) {
            displayResult = "";
        } else if (infixExpression.get(0).equals("-") && infixExpression.size() < 4) {
            //先检测第一个元素是否是负号且少于4个，不能计算
            displayResult = "";
        } else if (!infixExpression.get(0).equals("-") && infixExpression.size() < 3) {
            //第一个元素不是负号且小于4个，不能计算
            displayResult = "";
        } else if (lastInput.matches("[+\\-*/.]")) {
            //最后一个元素是非数字

            ArrayList<String> tempExp = new ArrayList<>();

            if (lastInput.equals(".")) {
                //最后输入的是小数点
                for (int i = 0; i <= infixExpression.size() - 1; i++) {
                    tempExp.add(infixExpression.get(i));
                }
                StringBuilder stringToFix = new StringBuilder(tempExp.remove(tempExp.size()-1));
                stringToFix.deleteCharAt(stringToFix.length()-1);
                tempExp.add(stringToFix.toString());
            } else {
                //最后输入的是加减乘除
                for (int i = 0; i <= infixExpression.size() - 2; i++) {
                    tempExp.add(infixExpression.get(i));
                }
            }

            if (tempExp.get(0).equals("-")) {
                //若第一个元素是负号
                if (tempExp.size() >= 4) {
                    //有多于4个元素可以计算
                    displayResult = calculatePostfix(infixToPostfix(tempExp));
                } else {
                    displayResult = "";
                }
            } else {
                if (tempExp.size() >= 3) {
                    //第一个元素不是负号，有多于三个元素可以计算
                    displayResult = calculatePostfix(infixToPostfix(tempExp));
                } else {
                    displayResult = "";
                }
            }
        } else {
            //最后一个元素不是符号
            //多于3三个元素可以计算
            if (infixExpression.size() >= 3) {
                displayResult = calculatePostfix(infixToPostfix(infixExpression));
            } else {
                displayResult = "";
            }
        }

        Log.i(TAG, "infix expression: " + infixExpression.toString() + " last input:" + lastInput);

        return displayResult;
    }

    private String displayExpressionBuilder() {
        StringBuilder expression = new StringBuilder();
        for (int i = 0; i <= infixExpression.size() - 1; i++) {
            String string = infixExpression.get(i);
            if (string.matches("[*]")) {
                expression.append("x");
            } else if (string.matches("/")) {
                expression.append("÷");
            } else {
                expression.append(infixExpression.get(i));
            }
        }
        return expression.toString();
    }


}
