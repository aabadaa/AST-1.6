package com.abada.ast;

import java.util.TreeSet;


class TruthTable   {
    static final String OPS = "¬∨⇔∧⇒⊕";
    String out = "";
    String in;
    String[][] cases =null;
    TreeSet<String> tempCases= new TreeSet<>((o1, o2) -> {
        if (o1.length() == o2.length() && !o1.equals(o2))
            return o1.compareTo(o2);
        else
            return o1.length() - o2.length();
    });

    public  TruthTable(String i) {
        in = i;
    }

    public String[][] getSchedule() {
        int sc = Ccount(in);
        int v = (int) Math.pow(2, sc);
        add(in);
        cases = new String[v + 1][tempCases.size()];
        z(cases);
        tempCases.toArray(cases[0]);
        simpleCs( sc, v);
        theEnd( sc);
        PA();
        System.out.println(out);
        return cases;
    }

    void simpleCs( int sc, int v) {
        int k, n = v;
        for (int i = 0; i < sc; i++) {
            n /= 2;//the first column has v/2 the next has v/2/2 ....
            k = n;
            String t = "T";
            for (int j = 1; j <= v; j++) {        // simple cases values;
                if (j <= k) {
                    cases[j][i] = t;
                } else {
                    k += n;
                    t = (t.equals("F")) ? "T" : "F";
                    cases[j][i] = t;
                }
            }
        }

    }

    int Ccount(String in) {
        String c = "";
        int count = 0;

        for (int i = 0; i < in.length(); i++) {
            if (!c.contains(in.charAt(i) + "") && !(OPS + "()").contains(in.charAt(i) + "")) {//c.matches("[a-z]")&&!contains
                count++;
            }
            c += in.charAt(i);
        }
        return count;
    }


    public String PA() {
        out="";
        for (int i = 0; i <= cases[0].length-1; i++) {
            out += cases[0][i] + ps((cases[0][i].length() % 2 == 0) ? 1 : 0) + "|";
        }
        out += "\n";
        for (int i = 1; i < cases.length; i++, out += "\n") {
            for (int j = 0; j <= cases[0].length-1; j++) {
                out += ps((int) Math.floor(((cases[0][j].length()+count(cases[0][j])) / 2)));
                out+=cases[i][j];
                out += ps((int) Math.floor(((cases[0][j].length()+count(cases[0][j])) / 2)));
                out += "|";
            }
        }
        return out;
    }

    void add(String in) {
        while (checkarcs(in)) {// in order to not retain arcs in cases
            in = in.substring(1, in.length() - 1);//removing arcs((((...))))>> ...
        }
        int temp=tempCases.size();
        tempCases.add(in);//that means this case is added before so no need to add int again
        if(temp==tempCases.size())
            return;
        //I have to manipulate ! operator to finish this method
        int arc = 0, i;
        for (i = in.length() - 1; i >= 0; i--) {
            if (OPS.substring(1).contains(in.charAt(i) + "") && arc == 0)//I removed the arcs in 95 just for this condition and this condition is just for devide the case to simpler
            {
                //  System.out.println("I am here " + in.substring(i + 1));
                add( in.substring(i + 1));
                add( in.substring(0, i));
                return;
            } else if (in.charAt(i) == '(') {
                arc++;
            } else if (in.charAt(i) == ')') {
                arc--;
            }

        }
        if (in.startsWith(OPS.charAt(0) + "")) {// to complete all operators
            in = in.substring(1);
            add( in);
        }
    }

    int search(String in[], String s) {
        while (checkarcs(s))// in order to not retain arcs in cases
        {
            s = s.substring(1, s.length() - 1);
        }
        for (int i = 0; i < in.length; i++) {
            if (s.equals(in[i])) {
                return i;
            }
        }
        return -1;
    }

    int getcases(String in, String out[]) {

        if (checkarcs(in.substring(1)) || in.length() == 2) {//if!()||!q
            out[0] = out[1] = in.substring(1);
            return 0;
        } else {
            int a = 0, i;
            for (i = in.length() - 1; i >= 0; i--) {
                if (in.charAt(i) == '(') {
                    a++;
                } else if (in.charAt(i) == ')') {
                    a--;
                }
                if (a == 0 && OPS.substring(1).contains(in.charAt(i) + "")) {
                    break;
                }
            }
            out[0] = in.substring(0, i);
            out[1] = in.substring(i + 1);
            return i;
        }
    }

    boolean checkarcs(String in) {//this method for startswith arcs return true when () not when ()()
        if (!in.startsWith("(")) {
            return false;
        }
        int a = 0;
        int i;
        for (i = 0; i < in.length(); i++) {
            if (in.charAt(i) == '(') {
                a++;
            } else if (in.charAt(i) == ')') {
                a--;
            }
            if (a == 0 && i != in.length() - 1) {//that means the case is like that ()() not like that (()())
                return false;
            }
        }
        return true;
    }


    void theEnd( int sc) {
        int leftIndex;
        int rightIndex;
        int oi;
        String cs[] = new String[2];
        for (int j = sc; j <=cases[0].length-1; j++)//the case
        {
            oi = getcases(cases[0][j], cs);
            leftIndex = search(cases[0], cs[0]);
            rightIndex = search(cases[0], cs[1]);
            for (int i = 1; i < cases.length; i++)//the values
            {
                switch (cases[0][j].charAt(oi)) {
                    case '⇒':
                        cases[i][j] = (cases[i][leftIndex].equals("T") && cases[i][rightIndex].equals("F")) ? "F" : "T";
                        break;
                    case '⇔':
                        cases[i][j] = (cases[i][leftIndex].equals(cases[i][rightIndex])) ? "T" : "F";
                        break;
                    case '∧':
                        cases[i][j] = (cases[i][leftIndex].equals("T") && cases[i][rightIndex].equals("T")) ? "T" : "F";
                        break;
                    case '∨':
                        cases[i][j] = (cases[i][leftIndex].equals("T") || cases[i][rightIndex].equals("T")) ? "T" : "F";
                        break;
                    case '¬':
                        cases[i][j] = (cases[i][leftIndex].equals("F")) ? "T" : "F";
                        break;
                    case '⊕':
                        cases[i][j] = (!cases[i][leftIndex].equals(cases[i][rightIndex])) ? "T" : "F";
                        break;
                }
            }
        }
    }

    String ps(int m) {
        String ps = "";
        while (m != 0) {
            ps += " ";
            m--;
        }
        return ps;
    }

    int count(String in) {
        int a = 0;
        for (int i = 0; i < in.length(); i++) {
            if (in.charAt(i) == '⇔' || in.charAt(i) == '⇒') {
                System.out.println("I am here");
                a++;
            }
            else
                System.out.println("I am not");
        }
        return a;
    }

    void z(String in[][]) {
        for (int i = 0; i < in.length; i++) {
            for (int j = 0; j < in[0].length; j++) {
                in[i][j] = "";
            }
        }
    }
}
