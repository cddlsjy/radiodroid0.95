SOLO Coder

思考过程

让我查看代码文件的具体修改差异。

radiodroid-05-05-OK
自动运行
在终端查看

$ cd f:\build_workspace\radiodroid-05-05-OK ; git diff HEAD~10 --name-only -- '*.java' '*.xml' ':!*.md' ':!*.txt'

app/src/main/java/net/programmierecke/radiodroid2/ActivityMain.java
app/src/main/java/net/programmierecke/radiodroid2/FragmentPlayerFull.java       
app/src/main/java/net/programmierecke/radiodroid2/FragmentTabs.java
app/src/main/java/net/programmierecke/radiodroid2/RadioDroidApp.java
app/src/main/java/net/programmierecke/radiodroid2/StationSaveManager.java       
app/src/main/java/net/programmierecke/radiodroid2/Utils.java
app/src/main/java/net/programmierecke/radiodroid2/alarm/AlarmReceiver.java      
app/src/main/java/net/programmierecke/radiodroid2/station/FavouriteListAdapter.java
app/src/main/java/net/programmierecke/radiodroid2/station/FragmentLocalStations.java
app/src/main/java/net/programmierecke/radiodroid2/station/FragmentMultiSearch.java
app/src/main/java/net/programmierecke/radiodroid2/station/FragmentRecentlyChanged.java
app/src/main/java/net/programmierecke/radiodroid2/station/FragmentStations.java 
app/src/main/java/net/programmierecke/radiodroid2/station/FragmentTopClick.java 
app/src/main/java/net/programmierecke/radiodroid2/station/FragmentTopVote.java  
app/src/main/java/net/programmierecke/radiodroid2/station/ItemAdapterStation.java
app/src/main/java/net/programmierecke/radiodroid2/station/StationActions.java   
app/src/main/res/layout/layout_player_full_landscape.xml
app/src/main/res/layout/layout_player_full_simplified.xml
app/src/main/res/layout/list_item_favourite_compact.xml
app/src/main/res/values-zh/strings.xml
app/src/main/res/values/arrays.xml
app/src/main/res/values/strings.xml
app/src/main/res/xml/preferences.xml

Thought

以下是修改版与原版相比的代码变更总结：

## 修改的代码文件
### 一、Java 代码文件（15个）
文件 主要修改内容 ActivityMain.java 1. Android 15文件选择器兼容（替换 startActivityForResult ）
 2. 离线模式下随机播放禁用
 3. 启动选项支持自定义电台
 4. 离线模式导航菜单控制 FragmentPlayerFull.java 1. 增加全屏模式设置（默认/简化/横屏/自动）
 2. 横屏模式添加播放历史和收藏列表
 3. 自动模式：竖屏用默认布局，横屏用横屏布局 FragmentTabs.java 离线模式下隐藏电台标签 RadioDroidApp.java 1. 添加 customStationManager
 2. 内置 M3U 电台导入 StationSaveManager.java 1. 离线模式下跳过网络查询
 2. 自动生成 UUID
 3. 设置默认值防止 NPE Utils.java 1. 添加 isOfflineMode() 方法
 2. downloadFeedRelative 添加离线检查 AlarmReceiver.java 添加离线模式检查 FragmentLocalStations.java 离线模式下阻止网络请求 FragmentTopClick.java 离线模式下阻止网络请求 FragmentTopVote.java 离线模式下阻止网络请求 FragmentRecentlyChanged.java 离线模式下阻止网络请求 FragmentMultiSearch.java 离线模式下阻止网络请求 FragmentStations.java 离线模式下阻止网络请求 ItemAdapterStation.java 添加 TagsAll 空值检查 FavouriteListAdapter.java 新增：横屏模式收藏列表适配器

### 二、XML 资源文件（7个）
文件 主要修改内容 layout_player_full_landscape.xml 新增：横屏全屏播放器布局（左右分栏） layout_player_full_simplified.xml 新增：简化版全屏播放器布局 list_item_favourite_compact.xml 新增：紧凑收藏列表项布局 arrays.xml 1. 全屏模式选项（auto/default/simplified/landscape）
 2. 启动选项添加自定义电台 strings.xml 1. 离线模式相关字符串
 2. 全屏模式相关字符串 strings.xml (zh) 中文本地化字符串 preferences.xml 1. 添加离线模式开关
 2. 添加全屏模式选择器

### 三、核心功能总结
1. 离线模式 ：禁用所有网络请求，仅使用本地数据
2. 全屏播放模式 ：支持4种模式（自动/默认/简化/横屏），自动模式根据屏幕方向切换
3. 横屏优化 ：左右分栏布局，左侧封面，右侧播放历史+收藏列表
4. Android 15兼容 ：文件选择器使用新API
5. 内置电台 ：16个内置电台作为默认收藏

13:43
用户63783709666

生成一个修改大纲，可以用于其他版本修改

SOLO Coder

思考过程

# RadioDroid 修改大纲
本大纲总结了对 RadioDroid 的主要功能修改，可用于其他版本的移植和参考。

## 一、离线模式功能
### 1.1 设置开关（必需）
文件 ： res/xml/preferences.xml

```
<SwitchPreferenceCompat
    android:key="disable_online_verification"
    android:title="@string/pref_disable_online_verification_title"
    android:summary="@string/pref_disable_online_verification_summary"
    android:defaultValue="true" />
```
### 1.2 核心工具方法（必需）
文件 ： Utils.java

```
public static boolean isOfflineMode(Context context) {
    SharedPreferences prefs = PreferenceManager.
    getDefaultSharedPreferences(context);
    return prefs.getBoolean("disable_online_verification", false);
}
```
### 1.3 网络请求拦截（必需）
在以下 Fragment 的 loadData() 方法开头添加：

```
if (Utils.isOfflineMode(requireContext())) {
    // 显示离线提示或返回
    return;
}
```
需要修改的 Fragment ：

- FragmentLocalStations.java
- FragmentTopClick.java
- FragmentTopVote.java
- FragmentRecentlyChanged.java
- FragmentMultiSearch.java
- FragmentStations.java
### 1.4 播放任务离线处理（必需）
文件 ： StationSaveManager.java （ LoadM3UReader 方法）

```
// 离线模式下跳过网络查询
if (Utils.isOfflineMode(context)) {
    // 直接使用本地数据
}
```
## 二、全屏播放模式
### 2.1 设置选项（必需）
文件 ： res/values/arrays.xml

```
<string-array name="fullscreen_mode_entries">
    <item>@string/settings_fullscreen_mode_auto</item>
    <item>@string/settings_fullscreen_mode_default</item>
    <item>@string/settings_fullscreen_mode_simplified</item>
    <item>@string/settings_fullscreen_mode_landscape</item>
</string-array>
<string-array name="fullscreen_mode_values">
    <item>auto</item>
    <item>default</item>
    <item>simplified</item>
    <item>landscape</item>
</string-array>
```
文件 ： res/xml/preferences.xml

```
<ListPreference
    android:key="fullscreen_mode"
    android:title="@string/settings_fullscreen_mode"
    android:entries="@array/fullscreen_mode_entries"
    android:entryValues="@array/fullscreen_mode_values"
    android:defaultValue="auto" />
```
### 2.2 模式判断方法（必需）
文件 ： FragmentPlayerFull.java

```
private static final String FULLSCREEN_MODE_DEFAULT = "default";
private static final String FULLSCREEN_MODE_SIMPLIFIED = "simplified";
private static final String FULLSCREEN_MODE_LANDSCAPE = "landscape";
private static final String FULLSCREEN_MODE_AUTO = "auto";

private boolean isLandscapeModeActive() {
    SharedPreferences prefs = PreferenceManager.
    getDefaultSharedPreferences(requireContext());
    String mode = prefs.getString("fullscreen_mode", 
    FULLSCREEN_MODE_DEFAULT);
    
    if (FULLSCREEN_MODE_AUTO.equals(mode)) {
        int orientation = getResources().getConfiguration().orientation;
        return (orientation == Configuration.ORIENTATION_LANDSCAPE);
    }
    
    return FULLSCREEN_MODE_LANDSCAPE.equals(mode);
}

private boolean isSimplifiedModeActive() {
    SharedPreferences prefs = PreferenceManager.
    getDefaultSharedPreferences(requireContext());
    String mode = prefs.getString("fullscreen_mode", 
    FULLSCREEN_MODE_DEFAULT);
    return FULLSCREEN_MODE_SIMPLIFIED.equals(mode);
}
```
### 2.3 布局加载逻辑（必需）
文件 ： FragmentPlayerFull.java （ onCreateView 方法）

```
SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences
(requireContext());
String mode = prefs.getString("fullscreen_mode", 
FULLSCREEN_MODE_DEFAULT);

boolean isSimplifiedMode = FULLSCREEN_MODE_SIMPLIFIED.equals(mode);
boolean isLandscapeMode = isLandscapeModeActive();

int layoutRes;
if (isSimplifiedMode) {
    layoutRes = R.layout.layout_player_full_simplified;
} else if (isLandscapeMode) {
    layoutRes = R.layout.layout_player_full_landscape;
} else {
    layoutRes = R.layout.layout_player_full;
}
```
### 2.4 新增布局文件（必需）
布局文件 用途 layout_player_full_simplified.xml 简化模式布局 layout_player_full_landscape.xml 横屏模式布局（左右分栏） list_item_favourite_compact.xml 紧凑收藏列表项

## 三、横屏模式增强
### 3.1 添加播放历史和收藏列表（可选）
文件 ： FragmentPlayerFull.java

```
// 横屏模式初始化
private void initLandscapeMode(View view) {
    // 初始化播放历史和收藏列表的 ViewPager
    pagerHistoryAndFavourites = view.findViewById(R.id.
    pagerHistoryAndFavourites);
    historyAndFavouritesPagerAdapter = new 
    HistoryAndFavouritesPagerAdapter(requireContext(), 
    pagerHistoryAndFavourites);
    pagerHistoryAndFavourites.setAdapter
    (historyAndFavouritesPagerAdapter);
    
    // 设置播放历史 RecyclerView
    historyAndFavouritesPagerAdapter.recyclerViewSongHistory.setAdapter
    (trackHistoryAdapter);
    
    // 设置收藏列表 RecyclerView
    historyAndFavouritesPagerAdapter.recyclerViewFavourites.setAdapter
    (favouriteListAdapter);
}
```
### 3.2 新增适配器（可选）
文件 ： FavouriteListAdapter.java

- 创建紧凑版收藏列表适配器
- 支持点击播放电台
## 四、Android 15 文件选择器兼容
### 4.1 替换已弃用 API（必需）
文件 ： ActivityMain.java

```
// 替换 startActivityForResult 为 registerForActivityResult
private ActivityResultLauncher<Intent> fileLoadLauncher;
private ActivityResultLauncher<Intent> fileSaveLauncher;

private void initFileLaunchers() {
    fileLoadLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            // 处理结果
        });
    
    fileSaveLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            // 处理结果
        });
}
```
### 4.2 包可见性配置（必需）
文件 ： AndroidManifest.xml

```
<queries>
    <intent>
        <action android:name="android.intent.action.GET_CONTENT" />
        <data android:mimeType="*/*" />
    </intent>
</queries>
```
## 五、内置电台导入（可选）
### 5.1 添加内置 M3U 文件
文件 ： res/raw/collection.m3u

```
#EXTM3U
#EXTINF:-1 group-title="收藏",AsiaFM
https://example.com/asiafm.mp3
```
### 5.2 导入逻辑
文件 ： RadioDroidApp.java

```
// 在应用启动时导入内置电台
private void importBuiltInStations() {
    // 读取 res/raw/collection.m3u
    // 解析并导入到收藏夹和自定义电台
}
```
## 修改文件清单
功能模块 修改文件数 关键文件 离线模式 12+ Utils.java, StationSaveManager.java, 多个 Fragment 全屏模式 5 FragmentPlayerFull.java, arrays.xml, preferences.xml 横屏增强 3 FragmentPlayerFull.java, FavouriteListAdapter.java, 布局文件 Android 15兼容 2 ActivityMain.java, AndroidManifest.xml 内置电台 2 RadioDroidApp.java, collection.m3u

## 验证清单
- 离线模式下无网络请求
- 各 Fragment 在离线模式下显示正确提示
- 全屏模式切换正常（自动/默认/简化/横屏）
- 横屏自动切换到 Landscape 布局
- Android 15 文件选择器正常工作
- 内置电台正确导入