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
