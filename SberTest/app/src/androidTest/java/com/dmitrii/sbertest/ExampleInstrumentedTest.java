package com.dmitrii.sbertest;

import android.content.Context;
import android.os.Parcel;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.dmitrii.sbertest", appContext.getPackageName());
    }

    @Test
    public void valuteParcel() throws Exception {
        CbrXmlParser.Valute valute = new CbrXmlParser.Valute();
        valute.name = "123";
        valute.value = "35,1";
        Parcel parcel = Parcel.obtain();
        valute.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        CbrXmlParser.Valute valute2 = CbrXmlParser.Valute.CREATOR.createFromParcel(parcel);
        assertEquals(valute, valute2);
    }


    @Test
    public void valuteListParcels() throws Exception {
        CbrXmlParser.ValuteList list = new CbrXmlParser.ValuteList();
        list.valuteList = new ArrayList<>();

        for (int i = 0; i < 10; ++i) {
            CbrXmlParser.Valute valute = new CbrXmlParser.Valute();
            valute.name = "123" + i;
            valute.value = "35,1" + i;
            list.valuteList.add(valute);
        }
        Parcel parcel = Parcel.obtain();
        list.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        CbrXmlParser.ValuteList list2 = CbrXmlParser.ValuteList.CREATOR.createFromParcel(parcel);
        assertEquals(list, list2);
    }

    @Test
    public void valuteListStore() throws Exception {
        CbrXmlParser.ValuteList list = new CbrXmlParser.ValuteList();
        list.valuteList = new ArrayList<>();

        for (int i = 0; i < 10; ++i) {
            CbrXmlParser.Valute valute = new CbrXmlParser.Valute();
            valute.name = "123" + i;
            valute.value = "35,1" + i;
            list.valuteList.add(valute);
        }

        Context context = InstrumentationRegistry.getTargetContext();
        assertTrue(MainPrefs.saveValutesList(context, list));
        CbrXmlParser.ValuteList list2 = MainPrefs.getValutesList(context);
        assertEquals(list, list2);
    }
}
