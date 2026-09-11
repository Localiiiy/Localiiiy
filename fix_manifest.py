with open("app/src/main/AndroidManifest.xml", "r") as f:
    text = f.read()

wrong_block = """    <application
        <!-- Sample AdMob App ID -->
        <meta-data
            android:name="com.google.android.gms.ads.APPLICATION_ID"
            android:value="ca-app-pub-3940256099942544~3347511713" />"""

correct_block = """    <application"""

text = text.replace(wrong_block, correct_block)

insert_target = """        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="AIzaSyLocaliiiyiiyMapsRadarApiKeyForDev" />"""

insert_replacement = """        <!-- Sample AdMob App ID -->
        <meta-data
            android:name="com.google.android.gms.ads.APPLICATION_ID"
            android:value="ca-app-pub-3940256099942544~3347511713" />
        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="AIzaSyLocaliiiyiiyMapsRadarApiKeyForDev" />"""

text = text.replace(insert_target, insert_replacement)

with open("app/src/main/AndroidManifest.xml", "w") as f:
    f.write(text)
