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
        return list;
    }
}
