<template>
  <div class="df-detector">
    <ServiceTitle :titleHeader="title"></ServiceTitle>
    <ServiceModule></ServiceModule>
    <ServiceDisplay :disImgs='disImgs' @uploadImage="uploadImage" ref="serviceDisplay"></ServiceDisplay>
    <ServiceUpload @uploadImage='uploadImage' @changeDisBoard="changeDisBoard" @uploadZip="uploadZip" ref="serviceUpload" :uploaded="uploaded" :downToZero="downToZero"></ServiceUpload>
  </div>
</template>
<!--components为啥要用大括号呢-->

<script>
import ServiceDisplay from '../components/ServiceDisplay'
import ServiceModule from '../components/ServiceModule'
import ServiceUpload from '../components/ServiceUpload'
import ServiceTitle from '../components/ServiceTitle'
import $ from 'jquery'

export default {
  name: 'DeepFakeDetector',
  components: {
    ServiceDisplay,
    ServiceModule,
    ServiceTitle,
    ServiceUpload
  },
  data () {
    return {
      title: 'DeepFake篡改检测',
      deepfakeDetector: window.server.DEEPFAKE,
      disImgs: [require('../../static/imgs/fake1.jpg'), require('../../static/imgs/fake2.jpg'), require('../../static/imgs/real1.jpg')],
      uploaded: 0
    }
  },
  methods: {
    uploadImage: function (imgs) { // 上传文件返回结果
      const _this = this
      _this.$refs.serviceDisplay.disBoardMask(true) // 给主显示框加上扫描效果
      const image = new Image()
      image.src = imgs.base64
      image.crossOrigin = ''
      $.ajax({
        type: 'post',
        url: _this.deepfakeDetector.detectUrl,
        dataType: 'json',
        contentType: 'application/json',
        data: JSON.stringify(imgs),
        success: function (response) {
          if (response.result) {
            // 通过base64字符串加载图片
            imgs.base64 = _this.drawDetections(image, response.data.rects)
            _this.$refs.serviceDisplay.updateDetectedImage(imgs)
          } else {
            // 显示错误信息
            _this.$message.warning(response.msg)
          }
        }
      })
    },
    drawDetections: function (img, detections) { // 绘制矩形框
      const canvas = document.createElement('canvas')
      canvas.width = img.width
      canvas.height = img.height
      const ctx = canvas.getContext('2d')
      ctx.drawImage(img, 0, 0, canvas.width, canvas.height)
      ctx.font = '20px Arial'
      detections.forEach(function (detection) {
        if (detection.type === 'fake') {
          ctx.strokeStyle = 'red' // 边框颜色
        } else {
          ctx.strokeStyle = 'green' // 边框颜色
        }
        ctx.lineWidth = 2 // 线条宽度
        ctx.strokeRect(detection.x1, detection.y1, detection.x2 - detection.x1, detection.y2 - detection.y1)
        const txt = '[ ' + detection.type + ', ' + detection.confidence + ' ]'
        ctx.lineWidth = 1 // 线条宽度
        ctx.strokeText(txt, detection.x1 + 3, detection.y2 - 5)
      })
      return canvas.toDataURL()
    },
    changeDisBoard: function (img) {
      this.$refs.serviceDisplay.changeDisboardToTask(img)
    },
    uploadZip: function (zipFile) {
      const _this = this
      $.ajax({
        type: 'post',
        url: _this.deepfakeDetector.detectUrl,
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
