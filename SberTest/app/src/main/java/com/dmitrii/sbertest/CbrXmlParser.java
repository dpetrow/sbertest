package com.dmitrii.sbertest;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Value;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dmitrii on 11.04.2017.
 */

public class CbrXmlParser {
    public static ValuteList parseStream(InputStream is) throws Exception {
        Reader reader = new InputStreamReader(is, "windows-1251");
        Persister persister = new Persister();
        return persister.read(ValuteList.class, reader);
    }

    @Root(name = "Valute", strict = false)
    public static class Valute implements Parcelable {
        @Element(name="Value")
        public String value;
        public float mValue;

        @Element(name="CharCode")
        public String code;

        @Element(name="Name")
        public String name;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(value);
            dest.writeString(name);
            dest.writeString(code);
        }

        public final static Parcelable.Creator<Valute> CREATOR = new Parcelable.Creator<Valute>() {
            @Override public Valute createFromParcel(Parcel source) {
                String value = source.readString();
                String name  = source.readString();
                String code  = source.readString();

                Valute valute = new Valute();
                valute.value = value;
                valute.name  = name;
                valute.code  = code;
                return valute;
            }

            @Override public Valute[] newArray(int size) {
                return new Valute[size];
            }
        };

        @Override public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Valute)) {
                return false;
            }
            return TextUtils.equals(value, ((Valute) obj).value) && TextUtils.equals(code, ((Valute) obj).code);
        }
    }

    @Root(name = "ValCurs", strict = false)
    public static class ValuteList implements Parcelable {
        @ElementList(inline=true, name="Valute")
        public List<Valute> valuteList;

        @Override public int describeContents() {
            return 0;
        }

        @Override public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(valuteList.size());
            for (Valute valute : valuteList) {
                dest.writeParcelable(valute, 0);
            }
        }

        public final static Parcelable.Creator<ValuteList> CREATOR = new Parcelable.Creator<ValuteList>() {
            @Override public ValuteList createFromParcel(Parcel source) {
                ValuteList valuteList = new ValuteList();
                int size = source.readInt();

                valuteList.valuteList = new ArrayList<>();
                for (int i = 0; i < size; ++i) {
                    Valute valute = source.readParcelable(Valute.class.getClassLoader());
                    valuteList.valuteList.add(valute);
                }
                return valuteList;
            }

            @Override public ValuteList[] newArray(int size) {
                return new ValuteList[size];
            }
        };

        @Override public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof ValuteList)) {
                return false;
            }
            ValuteList o = (ValuteList)obj;
            if (valuteList == null && o.valuteList == null) {
                return true;
            }
            if ((valuteList == null && o.valuteList != null) ||
                    (valuteList != null && o.valuteList == null)) {
                return false;
            }

            if (valuteList.size() !=  o.valuteList.size()) return false;

            for (Valute item : valuteList) {
                if (!o.valuteList.contains(item)) {
                    return false;
                }
            }

            return true;
        }
    }

}
