package com.mythmc.tools.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mythmc.externs.hook.placeholders.PlaceholderHook;
import com.mythmc.tools.utils.type.OperatorType;
import org.bukkit.entity.Player;

public class ConditionUtil {
    private static final Pattern PATTERN = Pattern.compile("(.+?)([<>!=]+)(.+)");
    public static boolean checkCondition(String condition) {
        // 解析条件字符串
        Matcher matcher = PATTERN.matcher(condition);

        if (matcher.find()) {
            // 解析要比较的部分
            String leftOperand = matcher.group(1).trim();
            String operatorSymbol = matcher.group(2).trim();
            String rightOperand = matcher.group(3).trim();

            OperatorType operatorType;
            try {
                operatorType = OperatorType.fromString(operatorSymbol);
            } catch (IllegalArgumentException e) {
                // 如果操作符无效，返回 false
                return false;
            }

            try {
                // 尝试将操作数转换为数字进行比较
                double leftValue = Double.parseDouble(leftOperand);
                double rightValue = Double.parseDouble(rightOperand);

                switch (operatorType) {
                    case EQUALS:
                        return leftValue == rightValue;
                    case NOT_EQUALS:
                        return leftValue != rightValue;
                    case GREATER_THAN:
                        return leftValue > rightValue;
                    case LESS_THAN:
                        return leftValue < rightValue;
                    case GREATER_THAN_OR_EQUAL:
                        return leftValue >= rightValue;
                    case LESS_THAN_OR_EQUAL:
                        return leftValue <= rightValue;
                    default:
                        return false;
                }
            } catch (NumberFormatException e) {
                // 如果无法转换为数字，则进行字符串比较
                switch (operatorType) {
                    case EQUALS:
                        return leftOperand.equals(rightOperand);
                    case NOT_EQUALS:
                        return !leftOperand.equals(rightOperand);
                    default:
                        return false;
                }
            }
        }

        // 如果条件不匹配，返回 false
        return false;
    }
}