# App과 App의 비동기 통신

App과 App 사이의 정보를 주고 받을 수 있는 방법은 다양하게 있습니다. `SharedPreferences`, `콘텐트 프로바이더`, `SQLite`, `BroadcastReceiver`,` intent`,  등 여러 방식으로 App 간의 데이터를 주고 받을 수 있습니다. 

이전에는 SharedPreferences 을 활용하여 간단하게 주고 받을 수 있었습니다.

```java
// SharedPreferences 을 공용으로 사용할 수 있는 소스코드
Context _context = null;
try {
    _context = createPackageContext("send target application package name", Context.CONTEXT_IGNORE_SECURITY);
} catch (PackageManager.NameNotFoundException e) {
    e.printStackTrace();
}

try {
    if (con != null) {
        SharedPreferences pref = _context.getSharedPreferences(
                "token_id", Context.MODE_WORLD_READABLE);

        String data = pref.getString("shared_token", "");
        Log.d("msg", "Other App Data: " + data);
    } else {
        Log.d("msg", "Other App Data: Context null");
    }
} catch (Exception e) {
    e.printStackTrace();
}
```

이전 버전에서 제공되었던 Context.MODE_WORLD_READABLE 의 사용을 API Level 17 에서 사용중지 하였습니다. 

![img](https://t1.daumcdn.net/cfile/tistory/999B63415A5F0F0E01)

위의 Android Developer 사이트에서 명시 되었듯이 ContentProvider, BroadcastRecevier, service 을 사용할 것을 권장하고 있습니다. 따라서 이번 공유 드릴 방식은 BroadcastReceiver 방식으로 App 과 App 비동기 통신 하는 방식을 공유 하겠습니다.

<br/>

# **BroadcastRecevier

BroadcastRecevier 를 사용하여 App과 App 간의 비동기로 데이터를 주고 받는 로직은 다음으로 구성하였습니다.

1. `BroadcastRecevier` 를 Server App(보내는 App), Client App(받는 앱) 두 앱 모두 `Action Name`을 등록합니다.
2. 등록한 `BroadcastRecevier`에 데이터를 전송합니다.
3. `BroadcastRecevier`의 onReceive() 발생 시 `SharedPreferences`에 전송받은 데이터를 저장합니다.

<br/>

#  **BroadcastRecevier  등록

BroadcastRecevier 등록하는 방법은 두 가지 입니다.  manifests에 등록하는 정적리시버, 등록과 해지가 가능한 동적 리시버 로 나눠집니다. 이번 포스트에서 사용한 BroadcastRecevier는 정적 리시버로 앱이 실행 시 BroadcastRecevier가 등록 되어 비동기 처리 할 수 있도록 구성하였습니다.

사용방법은 minifests에 등록만 하면 됩니다. `<application></application>` 사이에 `<receiver/>` 등록 하여 관리합니다 . 여기서 `<receiver/>`에 등록할 receiver name은 BroadcastReceiver을 상속받은 class을 입력합니다.

또한 **<uses-permission> .RECEIVE_BOOT_COMPLETED </uses-permission>** 을 선언하여 모바일 폰 재부팅 시에도 BroadcastRecevier 등록 상태를 유지 시켜 줍니다.

<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />

```xml
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />     <application                                                                                 		android:allowBackup="true"                                                                            		android:icon="@mipmap/ic_launcher"                                                                                          	  android:label="@string/app_name"                                                                                            		android:roundIcon="@mipmap/ic_launcher_round"                                                                             		android:supportsRtl="true"                                                                        		android:theme="@style/AppTheme">
    
    <receiver android:name=".TestBroadcastReceiver" >
        <intent-filter>
            <action android:name="kr.co.kcs.packagetestserver.client_one"/>
            <category android:name="android.intent.category.DEFAULT" />
        </intent-filter>
    </receiver>
    <!-- ... 밑에 부분 생략 -->
```

<br/>

# **BroadcastRecevier 데이터를 전송

데이터 전송부분은 sendBroadcast(Intent)을 사용하여 전송합니다.

```java
//Broadcast 전송
public void sendDateTime(Context ctx, String input) {
      Intent intent = new Intent(TestBroadcastReceiver.BROADCAST_SERVER);
      intent.putExtra(TestBroadcastReceiver.KEY_DATE, input);
      sendBroadcast(intent);
}
```

new Intent(String) 에서 String 에 해당하는 값은 BroadcastRecevier로 등록한 action 값을 등록하면 됩니다.

<br/>

# **SharedPreferences 저장**

BroadcastRecevier의 onReceive() 발생 시 SharedPreferences에 전송받은 데이터를 저장하는 로직을 구성할 것입니다. 

```java
public class TestBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "TestBroadcastReceiver";
    public static final String BROADCAST_SERVER = "kr.co.kcs.packagetestserver.server";
    public static final String BROADCAST_CLIENT0_ONE = "kr.co.kcs.packagetestserver.client_one";
    public static final String KEY_DATE = "date";

    @Override
    public void onReceive(Context context, Intent intent) {
        String name = intent.getAction();

        if(name.equals(BROADCAST_SERVER)){
            //SharedPreferences 에 데이터 저장 부분
            Utils.setDateTime(context, intent.getStringExtra(KEY_DATE));
        }
    }
}
```

<br/>

# **정리**

공유드린 3 가지 방식인 BroadcastReceiver 등록과 데이터 전송, SharedPreferences에 저장 하는 방식으로 구성 시 App 간 비동기 데이터 전송을 확인할 수 있습니다. 다른 방식인 콘텐트 프로바이더, 서비스 로 구성할 수 있으나 간편하게 사용 할 수 있는  BR 통신을 사용하였습니다. 
