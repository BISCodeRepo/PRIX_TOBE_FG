package com.prix.homepage.backend.livesearch.service.patternmatch;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;

public class Make_html {
    public Make_html() {
    }

    public String getOneProtein(String var1, String var2, String var3, String var4, String var5, boolean var6) throws IOException, SQLException {
        StringBuffer var7 = new StringBuffer();
        if (var1.equals("swiss_prot")) {
            var7.append("<p><b>AC# : <a href=http://www.uniprot.org/uniprot/");
            var7.append(var2);
            var7.append(" target=blank>");
            var7.append(var2);
            var7.append("</a></b><br>\n");
            var7.append("<b>Description : </b>");
        } else if (var1.equals("genbank")) {
            var7.append("<p><b>GI# : <a href=http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=protein&list_uids=");
            var7.append(var2);
            var7.append("&dopt=GenPept target=_blank>");
            var7.append(var2);
            var7.append("</a></b><br>\n");
            var7.append("<b>Definition : </b>");
        }

        var7.append(var3);
        var7.append("<br>");
        var7.append("<b>Species : </b>");
        var7.append(var4);
        var7.append("<br>");
        if (!var6) {
            var7.append("<b>Sequence : </b> \n");
            var7.append("<pre>\n   ");
            var7.append(this.Make_Space(var5));
            var7.append("\n</pre> \n \n ");
        }

        return var7.toString();
    }

    public String PrintParameter(String[] var1, int[] var2, String var3, boolean var4) throws IOException {
        int var5 = var1.length;
        StringBuffer var6 = new StringBuffer();
        var6.append("<b>*Search Pattern   : ");

        for(int var7 = 0; var7 < var5; ++var7) {
            var6.append(var1[var7]);
            if (var7 != var5 - 1) {
                var6.append(" AND ");
            }
        }

        var6.append("\n<br>*Search Protein   : ");
        var6.append(var2[0]);
        var6.append("\n<br>*Find Pattern   : ");
        var6.append(var2[1]);
        var6.append("\n<br>*Search Species   : ");
        if (var4) {
            var6.append(var3);
        }

        var6.append("</b> <br> \n ");
        return var6.toString();
    }

    private String Make_Space(String var1) {
        int var2 = var1.length();
        int var3 = 0;
        StringBuffer var4 = new StringBuffer();

        for(int var5 = 0; var5 < var2; ++var5) {
            if (var1.charAt(var5) != '<') {
                var4.append(var1.substring(var5, var5 + 1));
                ++var3;
                if (var3 % 100 == 0) {
                    var4.append("\n   ");
                } else if (var3 % 10 == 0) {
                    var4.append(" ");
                }
            } else {
                while(var1.charAt(var5) != '>') {
                    var4.append(var1.substring(var5, var5 + 1));
                    ++var5;
                }

                var4.append(">");
            }
        }

        return var4.toString();
    }


}
