package cn.crtlprototypestudios.ovsr.client.api;

public class OverseerUtility {
    public static String hiddenIndexString(String content, Object uniqueIndex){
        return String.format("%s##%s", content, String.valueOf(uniqueIndex));
    }
}
