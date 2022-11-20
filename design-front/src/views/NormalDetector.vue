<template>
  <div class="normal-detector">
    <ServiceTitle :titleHeader="title"></ServiceTitle>
    <ServiceModule2 @parentFunc='switchType'></ServiceModule2>
    <ServiceDisplay :disImgs='disImgs' :detectType="detectType" @uploadImage="uploadImage" ref="serviceDisplay"></ServiceDisplay>
    <ServiceUpload :detectType="detectType" @uploadImage='uploadImage' @changeDisBoard="changeDisBoard" :downToZero="downToZero"
                   :uploaded="uploaded"
                   @uploadZip="uploadZip"
                   ref="serviceUpload"></ServiceUpload>
  </div>
</template>

<script>
import ServiceDisplay from '../components/ServiceDisplay'
import ServiceModule2 from '../components/ServiceModule2'
import ServiceUpload from '../components/ServiceUpload'
import ServiceTitle from '../components/ServiceTitle'
import $ from 'jquery'

export default {
  name: 'DeepFakeDetector',
  components: {
    ServiceDisplay,
    ServiceModule2,
    ServiceTitle,
    ServiceUpload
  },
  data () {
    return {
      title: '普通篡改检测',
      normalDetector: window.server.NORMAL,
      disImgs: [require('../../static/imgs/copymove1.jpg'), require('../../static/imgs/copymove2.jpg'), require('../../static/imgs/copymove3.jpg')],
      detectType: 'copymove',
      uploaded: 0
    }
  },
  methods: {
    switchType: function (type) {
      this.disImgs = []
      this.detectType = type
      if (Object.is(type, 'copymove')) {
        this.disImgs = [require('../../static/imgs/copymove1.jpg'), require('../../static/imgs/copymove2.jpg'), require('../../static/imgs/copymove3.jpg')]
      } else if (Object.is(type, 'splicing')) {
        this.disImgs = [require('../../static/imgs/splicing1.jpg'), require('../../static/imgs/splicing2.jpg'), require('../../static/imgs/splicing3.jpg')]
      } else {
        this.disImgs = [require('../../static/imgs/copymove4.jpg'), require('../../static/imgs/splicing2.jpg'), require('../../static/imgs/copymove3.jpg')]
      }
    },
    uploadImage: function (imgs) { // 上传文件返回结果
      const _this = this
      _this.$refs.serviceDisplay.disBoardMask(true) // 给主显示框加上扫描效果
      // todo 待学习, 学习Image的使用 https://www.cnblogs.com/tianma3798/p/13508786.html
      const image = new Image()
      image.src = imgs.base64
      image.crossOrigin = ''
      // todo 学习 element-ui中确认消息框的使用， jquery需要引入脚本jq-confirm.js
      $.ajax({
        type: 'post',
        url: _this.normalDetector.detectUrl,
        dataType: 'json',
        contentType: 'application/json',
        data: JSON.stringify(imgs),
        success: function (response) {
          if (response.result) {
            // 通过base64字符串加载图片
            imgs.base64 = response.data
            // todo 待学习 父组件调用子组件的函数 https://www.cnblogs.com/effortandluck/p/16355992.html
            _this.$refs.serviceDisplay.updateDetectedImage(imgs)
          } else {
            // 显示错误信息
            _this.$message.warning(response.msg)
          }
        }
      })
    },
    changeDisBoard: function (img) {
      this.$refs.serviceDisplay.changeDisboardToTask(img)
    },
    uploadZip: function (zipFile) {
      const _this = this
      $.ajax({
        type: 'post',
        url: _this.normalDetector.detectUrl,
        dataType: 'json',
        contentType: 'application/json',
        data: JSON.stringify(zipFile),
        success: function (response) {
          if (response.result) {
            _this.$message.success('检测文本存储于: ' + response.data)
          } else {
            _this.$message.warning(response.msg)
          }
        },
        xhr: function () { // 显示加载进度
          const xhr = $.ajaxSettings.xhr()
          // todo 学习 使用XMLHttpRequest.upload监听上传过程，注册progress事件，打印回调函数中的event事件
          if (xhr.upload) {
            xhr.upload.addEventListener('progress', function (event) {
              // 已经上传的进度条
              _this.uploaded = Math.round(event.loaded / event.total * 100)
              console.log(this.uploaded)
            }, false)
          }
          return xhr
        }
      })
    },
    downToZero: function () {
      this.uploaded = 0
    }
  }
}
</script>
