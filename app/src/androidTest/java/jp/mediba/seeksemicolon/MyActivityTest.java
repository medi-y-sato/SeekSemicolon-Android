package jp.mediba.seeksemicolon;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * メイン画面のテスト
 * Created by ysato on 2015/08/10.
 */

@RunWith(AndroidJUnit4.class)
public class MyActivityTest {

    private Activity mActivity;

    @Rule
    public ActivityTestRule<MyActivity> mActivityRule =
            new ActivityTestRule<>(MyActivity.class);

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityRule.getActivity();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testOnCreate() throws Exception {
    }

    /**
     * @throws Exception
     * タイトルテキストとスタートボタンを出しているだけなのでテストケース無し
     * スタートボタンのテストはUIテストで
     */
    @Test
    public void testTitleScene() throws Exception {

    }

    @Test
    public void testGameScene() throws Exception {

    }

    @Test
    public void testMakeColonTable() throws Exception {

    }

    @Test
    public void testGameOverScene() throws Exception {

    }

    @Test
    public void testGetPatternCSV() throws Exception {

    }

    @Test
    public void testStartCount() throws Exception {

    }

    @Test
    public void testStopCount() throws Exception {

    }


}