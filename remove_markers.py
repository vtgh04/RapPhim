import os
import re

files = [
    r"c:\Users\ADMIN\Desktop\CinemaProject\RapPhim\src\main\java\com\rapphim\view\panels\HallPanel.java",
    r"c:\Users\ADMIN\Desktop\CinemaProject\RapPhim\src\main\java\com\rapphim\view\panels\Login.java",
    r"c:\Users\ADMIN\Desktop\CinemaProject\RapPhim\src\main\java\com\rapphim\view\panels\GeneralAdmin.java",
    r"c:\Users\ADMIN\Desktop\CinemaProject\RapPhim\src\main\java\com\rapphim\view\panels\GeneralStaff.java",
    r"c:\Users\ADMIN\Desktop\CinemaProject\RapPhim\src\main\java\com\rapphim\view\panels\MoviePanel.java",
    r"c:\Users\ADMIN\Desktop\CinemaProject\RapPhim\src\main\java\com\rapphim\view\panels\SettingPanel.java",
    r"c:\Users\ADMIN\Desktop\CinemaProject\RapPhim\src\main\java\com\rapphim\view\Main.java"
]

pattern1 = re.compile(r'^[ \t]*// ──.*?\r?\n', re.MULTILINE)
pattern2 = re.compile(r'^[ \t]*/\*\* Constructor mặc định \(dùng cho test UI độc lập\)\. \*/\r?\n', re.MULTILINE)
pattern3 = re.compile(r'^[ \t]*/\*\*\r?\n[ \t]*\* Constructor nhận Employee sau khi đăng nhập thành công\.\r?\n[ \t]*\*\r?\n[ \t]*\* @param employee nhân viên đã xác thực \(null = chế độ test\)\r?\n[ \t]*\*/\r?\n', re.MULTILINE)

for fpath in files:
    if os.path.exists(fpath):
        with open(fpath, "r", encoding="utf-8") as f:
            content = f.read()

        c2 = pattern1.sub('', content)
        c3 = pattern2.sub('', c2)
        c4 = pattern3.sub('', c3)

        if c4 != content:
            with open(fpath, "w", encoding="utf-8") as f:
                f.write(c4)
            print(f"Modified {fpath}")
        else:
            print(f"No changes in {fpath}")
    else:
        print(f"Not found: {fpath}")
