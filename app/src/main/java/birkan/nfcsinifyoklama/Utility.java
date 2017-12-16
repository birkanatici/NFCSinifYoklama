package birkan.nfcsinifyoklama;

import android.nfc.Tag;

/**
 * Created by birkan on 16.04.2017.
 */

public class Utility  {

    public static int STATE;

    public String dumpTagData(Tag tag) {
        StringBuilder sb = new StringBuilder();
        byte[] id = tag.getId();
        long result = 0;
        long factor = 1;

        for (int i = 0; i < id.length; ++i) {
            long value = id[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }

        sb.append(result);
        return sb.toString();
    }

}
