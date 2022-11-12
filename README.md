# DeepfakeApplication
this project is consisted of the face detector utilizing yolov5 model and the deepfake detector utilizing LGGP model

### this is still some problems required to be solve:
1.It's hard to two port to transport a very big zip file(such as xxxMB or GB), the main problem might that the file is too big to read as base64 string, which may be not suitable for tranfrom big files. 
We will try to use some other format to tranform big files or try to use websokect.

2. Problem 2: since the process time of backend is too long for batch process of big zip file, we need to display the working progress of the backend, making the user a better acknowledge of the running exe.

3. Problem 3: since it's hard to do batch process, we need to to multireading to process other users request/



## development log
### date 22.11.12
1. 编写了一个邮箱发送类(hjw)
2. 实现了ajax上传大文件时的进度条制作


**debug**:
1. 如果上传的压缩包时一个含有图片的文件夹，而不直接是图片，导致解压失败（已修复）
2. 上传超大文件，电脑死机
