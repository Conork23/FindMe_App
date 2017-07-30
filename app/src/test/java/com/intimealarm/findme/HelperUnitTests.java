package com.intimealarm.findme;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.intimealarm.findme.Utils.Helper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(TextUtils.class)
public class HelperUnitTests {

    Helper help;
    Context context;
    SharedPreferences sharedPrefs;

    @Before
    public void before(){
        this.help = new Helper();
        this.context = Mockito.mock(Context.class);
        this.sharedPrefs = Mockito.mock(SharedPreferences.class);
        Mockito.when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs);
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.when(TextUtils.isEmpty(any(CharSequence.class))).thenAnswer(new Answer<Boolean>() {

            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                CharSequence s = (CharSequence) invocation.getArguments()[0];
                boolean isEmpty = !(s != null && s.length() > 0);
                return isEmpty;
            }
        });

    }

    @Test
    public void helperDisableSMS() throws Exception {
        SharedPreferences.Editor editor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(sharedPrefs.edit()).thenReturn(editor);
        help.disableSMS(context);
        Mockito.verify(editor).apply();
    }

    @Test
    public void helperCheckFieldsPositive() throws Exception {
        String s = "This is a String";
        String s1 = "This is another String";
        String s2 = "This is yet another String";
        boolean pass = help.checkFields(s,s1,s2);
        assertEquals(true, pass);
    }

    @Test
    public void helperCheckFieldsNegative() throws Exception {
        String s = "";
        String s1 = " ";
        String s2 = null;
        boolean pass = help.checkFields(s,s1,s2);
        assertEquals(false, pass);
    }
}