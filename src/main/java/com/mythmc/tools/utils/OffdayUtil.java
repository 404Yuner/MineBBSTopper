package com.mythmc.tools.utils;

import com.mythmc.file.statics.ConfigFile;
import com.mythmc.tools.Debugger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class OffdayUtil {

    // Calendar current = Calendar.getInstance(); // 当前时间
    public static boolean isOffDayReward(Calendar current) {
        boolean result = false;

        for (String day : ConfigFile.offdays) {
            Pattern upcasePattern = Pattern.compile("^[A-Z]+$");// 全大写英文字符串
            Pattern datePattern = Pattern.compile("^\\d{2}-\\d{2}$");// 00-00格式的字符串
            if (upcasePattern.matcher(day).matches()) {// 如果是全大写英文字符
                Class<?> clazz = Calendar.class;
                int dayofweek = 0;
                try {
                    // 根据Calendar终态静态变量将字符串转换成星期
                    dayofweek = clazz.getField(day).getInt(null);

                } catch (Exception e) {// 非法参数
                    e.printStackTrace();
                }
                int dayofweekcurrent = current.get(Calendar.DAY_OF_WEEK);// 当前领奖的记录是星期几
                if (dayofweekcurrent == dayofweek) { // 如果当前就是设定的日子
                    result = true;
                    break;
                }
            } else if (datePattern.matcher(day).matches()) {// 如果是00-00这种字符串
                SimpleDateFormat offdayformat = new SimpleDateFormat("MM-dd");
                Calendar offdaycalendar = Calendar.getInstance();// 设定的假日日期(1970年的...)
                try {
                    Date offdaydate = offdayformat.parse(day);
                    offdaycalendar.setTime(offdaydate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // 如果当前领奖的记录的月份和日号与设定值一样
                if (current.get(Calendar.MONTH) == offdaycalendar.get(Calendar.MONTH)
                        && current.get(Calendar.DAY_OF_MONTH) == offdaycalendar.get(Calendar.DAY_OF_MONTH)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    public static List<String> handleCommands() {
        Calendar current = Calendar.getInstance();
        List<String> finalCommands = new ArrayList<>();

        // 如果开启了休息日奖励
        if (ConfigFile.enableOffdayReward && isOffDayReward(current)) {
            // 添加休息日奖励
            finalCommands.addAll(ConfigFile.offdayRewardCommands);
            Debugger.logger("检测到今天为配置的休息日，已将休息日奖励加入");
            // 如果是额外奖励，添加正常奖励
            if (ConfigFile.isExtraRewards) {
                finalCommands.addAll(ConfigFile.normalRewardCommands);
                Debugger.logger("检测到你开启了额外奖励，已将普通奖励加入");
            }
        } else {
            // 否则，只添加正常奖励
            finalCommands.addAll(ConfigFile.normalRewardCommands);
            Debugger.logger("检测到休息日条件不满足，只将普通奖励加入");
        }

        return finalCommands;
    }
}
