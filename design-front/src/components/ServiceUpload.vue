<template>
  <div class="service-upload">
<!--输入框1-->
    <div class="inb upload-input">
      <div class="inb input-box">
        <input id="filePath" :value="uploadImages.name" placeholder="请输入文件路径"/>
      </div>
      <div class="inb input-btn" @click="openWindow">
        <span style="display: inline-block;">本地上传</span>
      </div>
      <span class="upload-info">仅支持图片类型('png', 'jpg', 'jpeg', 'bmp', 'gif', 'webp')或压缩文件(.zip)</span>
    </div>
<!--上传按钮2-->
    <div class="inb upload-btn">
      <div class="detect-btn" @click="uploadFile">
        <span>检测</span>
      </div>
    </div>
<!--  进度条 -->
    <div class="progress" v-if="uploadZip">
      <div class="progress-bar progress-bar-success progress-bar-striped  active" role="progressbar" :aria-valuenow="uploaded" aria-valuemin="0" aria-valuemax="100" :style="{'width': String(uploaded) + '%','min-width': '5em'}">
        {{'上传进度' + String(uploaded) + '%'}}
      </div>
    </div>
    <input type="file" id="importFile" v-show="false">
  </div>
</template>

<script>
import $ from 'jquery'
export default {
  name: 'ServiceUpload',
  data () {
    return {
      uploadImages: {
        name: '',
        type: '',
        size: 0,
        base64: '',
        detectType: ''
      },
      uploadZip: false
    }
  },
  props: {
    uploaded: {
      type: Number,
      require: true,
      default: 0
    },
    downToZero: {
      type: Function
    },
    detectType: [String]
  },
  methods: {
    // todo 不能随便使用匿名函数的形式，因为可能会无法访问vue变量，因此
    openWindow: function () {
      document.getElementById('importFile').click()
    },
    check_suffix: function (filename) { // 检查文件类型
      const _this = this
      const fileType = filename.substring(filename.lastIndexOf('.') + 1)
      if (['png', 'jpg', 'jpeg', 'bmp', 'gif', 'webp'].indexOf(fileType.toLowerCase()) !== -1) {
        _this.$message.success('选中一张图片' + filename)
        return 1
      } else if (['zip'].indexOf(fileType.toLowerCase()) !== -1) {
        _this.$message.success('选中多个文件，进入批处理状态' + filename)
        return 2
      } else {
        return 0
      }
    },
    getBase64: function (file, isFinal) { // 获取文件字符串并获取文件对象
      const fileReader = new FileReader()
      const _this = this
      // todo 对象的构建 https://blog.csdn.net/LinDadaxia/article/details/107388546
      _this.uploadImages.name = file.name
      _this.uploadImages.size = file.size
      _this.uploadImages.type = file.type
      _this.uploadImages.detectType = this.detectType
      fileReader.readAsDataURL(file)
      fileReader.onload = function (event) { // 图片加载完成之后需要提示,并更新主框的视图
        _this.uploadImages.base64 = event.target.result
        if (_this.check_suffix(file.name) === 1) { // 显示图片
          _this.$emit('changeDisBoard', _this.uploadImages)
        } else {
          _this.downToZero()
        }
        if (isFinal) {
          // todo js字符串格式化输出 https://blog.csdn.net/abraham_ly/article/details/111150401?ops_request_misc=&request_id=&biz_id=102&utm_term=js%E6%A0%BC%E5%BC%8F%E5%8C%96%E8%BE%93%E5%87%BA&utm_medium=distribute.pc_search_result.none-task-blog-2~all~sobaiduweb~default-0-111150401.nonecase&spm=1018.2226.3001.4187
          // todo 格式化输出数字 https://blog.csdn.net/weixin_30258621/article/details/112933672?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522166744417916782390552245%2522%252C%2522scm%2522%253A%252220140713.130102334.pc%255Fall.%2522%257D&request_id=166744417916782390552245&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~first_rank_ecpm_v1~rank_v31_ecpm-2-112933672-null-null.142^v62^pc_search_tree,201^v3^control_2,213^v1^control&utm_term=js%E6%A0%BC%E5%BC%8F%E5%8C%96%E8%BE%93%E5%87%BA%E6%9C%89%E6%95%88%E6%95%B0%E5%AD%97&spm=1018.2226.3001.4187
          _this.$message.success(`成功选中${_this.uploadImages.name}。\n总大小为: ${Number(_this.uploadImages.size / 1024.0).toFixed(2)}kb;`)
        }
      }
    },
    uploadFile: function (event) {
      const _this = this
      if (_this.uploadImages.size === 0) {
        _this.$message.warning('未选中任何文件，无法检测')
      } else {
        // 向父组件申报上传函数
        this.$confirm('确认上传检测吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          _this.$message.info('正在上传')
          if (_this.check_suffix(_this.uploadImages.name) === 1) {
            _this.$emit('uploadImage', _this.uploadImages)
          } else {
            _this.uploadZip = true
            _this.$emit('uploadZip', _this.uploadImages)
          }
        }).catch(() => {
          _this.$message.info('已经取消上传')
        })
      }
    }
  },
  mounted () {
    const _this = this
    // 当上传文件时触发的内容
    const fn = function (event) { // 选择文件之后的加载函数
      const files = event.target.files
      _this.uploadImages.size = 0
      if (files.length > 1) {
        _this.$message.warning('仅支持单张图片处理, 多张图片请压缩成zip文件。')
      } else {
        // 对进度条的处理
        if (_this.check_suffix(files[0].name) !== 2) {
          _this.uploadZip = false
        }
        if (_this.check_suffix(files[0].name) !== 0) {
          // todo 文件需要先加载，加载完成之后才能触发事件，这是一个异步函数 https://blog.csdn.net/lyx32609/article/details/112601241
          _this.getBase64(files[0], true)
        } else {
          _this.$message.warning('仅支持图片或者.zip结尾的压缩文件夹')
        }
      }
    }
    $('#importFile').change(fn)
  }
}
</script>

<style scoped>
.service-upload{
  height: 40px;
  padding: 3px 0;
  text-align:left;
  font-size: 16px;
}
.inb{
  display: inline-block;
}
.upload-input{
  width: 78%;
  margin-right:2%;
  padding-left: 20px;
}
.input-btn{
  background-color: #007FF5;
  height: 35px;
  width: 20%;
  float: right;
  color: #fff;
  font-weight: bold;
  border-bottom-right-radius: 17px;
  border-top-right-radius: 17px;
}
/*输入框的外围*/
.input-box{
  border-bottom-left-radius:18px;
  border-top-left-radius:18px;
  border: 2px solid #A1AFD2;
  height: 35px;
  width:80%;
  color: #43485C;
  background-color: #fff;
}
/*实际的输入框*/
.input-box input{
  width:90%;
  margin-top:4px;
  margin-left: 20px;
  border-style: none;
}

/*文字框，控制文字的位置*/
.input-btn span{
  display: block;
  margin-top: 6px;
  margin-left: 20%;
}
.input-btn span:hover{
  cursor: pointer;
}
.upload-btn{
   width: 18%;
  vertical-align: top;
}
.detect-btn{
  width: 130px;
  height: 35px;
  text-align: center;
  background-color: #007FF5;
  color: #fff;
  border-radius: 17px;
  font-weight: bold;
  margin-left: 40px;
}
.detect-btn:hover{
  cursor: pointer;
}
.detect-btn span{
  display: inline-block;
  margin-top: 6px;
}
.upload-info{
  color: #B9BBC3;
  font-size: 14px;
  display: block;
  margin-left: 15px;
}
.progress{
  width: 95%;
  margin: 5px auto 50px;
  height: 17px;
}
</style>
