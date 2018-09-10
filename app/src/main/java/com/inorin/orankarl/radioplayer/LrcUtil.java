package com.inorin.orankarl.radioplayer;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LrcUtil {
    /**
     * Thanks to https://github.com/lenve/LrcView
     * Receive text of a lrc-type file
     * Return a list of LrcBean
     * @param lrcStr
     * @return
     */
    public static int charNumPerLine = 15;

    public static List<LrcBean> parseStr2List(String lrcStr) {
        List<LrcBean> list = new ArrayList<>();
        String lrcText = lrcStr.replaceAll("&#58;", ":")
                .replaceAll("&#10;", "\n")
                .replaceAll("&#46;", ".")
                .replaceAll("&#32;", " ")
                .replaceAll("&#45;", "-")
                .replaceAll("&#13;", "\r").replaceAll("&#39;", "'");
        String[] split = lrcText.split("\n");
        for (int i = 0; i < split.length; i++) {
            String lrc = split[i];
            if (lrc.contains(".")) {
                String min = lrc.substring(lrc.indexOf("[") + 1, lrc.indexOf("[") + 3);
                String seconds = lrc.substring(lrc.indexOf(":") + 1, lrc.indexOf(":") + 3);
                String mills = lrc.substring(lrc.indexOf(".") + 1, lrc.indexOf(".") + 3);
                long startTime = Long.valueOf(min) * 60 * 1000 + Long.valueOf(seconds) * 1000 + Long.valueOf(mills) * 10;
                String text = lrc.substring(lrc.indexOf("]") + 1);
                if (text == null || "".equals(text)) {
                    text = "music";
                }
                LrcBean lrcBean = new LrcBean();
                lrcBean.setStart(startTime);
                lrcBean.setLrc(text);
                list.add(lrcBean);
                if (list.size() > 1) {
                    list.get(list.size() - 2).setEnd(startTime);
                }
                if (i == split.length - 1) {
                    list.get(list.size() - 1).setEnd(startTime + 100000);
                }
            }
        }

        Collections.sort(list, new Comparator<LrcBean>() {
            @Override
            public int compare(LrcBean lrcBean, LrcBean t1) {
                return Long.compare(lrcBean.getStart(), t1.getStart());
            }
        });

        //Auto split a long line into several short line according to charNumPerLine
        List<LrcBean> newList = new ArrayList<>();
        for (int i = 0; i < list.size() - 1; i++) {
            String string = list.get(i).getLrc();
            if (string.length() < charNumPerLine) {
                newList.add(list.get(i));
                continue;
            }
            long startTime = list.get(i).getStart();
            long nextStartTime = list.get(i+1).getStart();
            long duration = nextStartTime - startTime;
            long time = startTime;
            int index = 0;
            while(index < string.length()) {
                int endIndex = Math.min(index + charNumPerLine, string.length());
                if (endIndex + 1 < string.length() && string.charAt(endIndex+1) == ' ') endIndex++;
                else if (endIndex - 2 > index && string.charAt(endIndex - 2) == ' ') endIndex--;
                long endTime = (long) (time + 1.0 * (endIndex - index) / string.length() * duration);
                String lrc = string.substring(index, endIndex);
                if (lrc.charAt(0) == ' ') lrc = lrc.substring(1);
                if (lrc.charAt(lrc.length() - 1) == ' ') lrc = lrc.substring(0, lrc.length()-1);
                LrcBean bean = new LrcBean(lrc, time, endTime);
                index = endIndex;
                time = endTime;
                newList.add(bean);
            }
        }

        return newList;
    }
}
