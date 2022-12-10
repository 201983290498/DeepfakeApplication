<template>
  <div class="service-display">
    <div class="display-main inb" id="mainBox">
      <img />
      <div class="dsno">
        <div class="scan" style="width:100%;height:100%;"></div>
      </div>
    </div>
    <div class="display-minor inb">
      <!--  图片框加上遮罩  -->
      <div class="example" id="example1" @click="changeDisboard">
        <img :src="disImgs[0]"/>
        <div class="dsno"></div>
      </div>
      <div class="example" id="example2" @click="changeDisboard">
        <img :src="disImgs[1]"/>
        <div class="dsno"></div>
      </div>
      <div class="example" id="example3" @click="changeDisboard">
        <img :src="disImgs[2]"/>
        <div class="dsno"></div>
      </div>
    </div>
  </div>
</template>

<script>
import $ from 'jquery'

export default {
  name: 'ServiceDisplay',
  data () {
    return {
      activeBox: 'example1'
    }
  },
  watch: {
    activeBox: function (newValue, oldValue) { // 根据活跃的值,自动更新选中的小阴影框
      if (oldValue !== '') {
        $('#' + oldValue + ' div').addClass('wrap-mask')
      }
      if (newValue !== '') {
        $('#' + newValue + ' div').removeClass('wrap-mask')
      }
    }
  },
  props: {
    disImgs: [],
    detectType: [String]
  },
  methods: {
    changeDisboard: function (event) {
      // todo 带学习 addClass和removeClass函数的正确用法, 存在不重复添加，不存在删除不报错
      const _this = this
      const activeBox = $(event.target).parent().attr('id')
      _this.activeBox = activeBox
      _this.getBase64($('#' + _this.activeBox + ' img').attr('src'))
    },
    getBase64 (imgUrl) {
      const _this = this
      const image = new Image()
      image.crossOrigin = ''
      image.src = imgUrl
      const deffered = $.Deferred()
      if (imgUrl) {
        image.onload = function () {
          const data = _this.getBase64Image(image)
          $('#mainBox img').attr('src', data)
          _this.upload(data, imgUrl)
          deffered.resolve(data)
        }
        return deffered.promise()
      }
    },
    getBase64Image (img) {
      const canvas = document.createElement('canvas')
      canvas.width = img.width
      canvas.height = img.height
      const ctx = canvas.getContext('2d')
      ctx.drawImage(img, 0, 0, canvas.width, canvas.height)
      return canvas.toDataURL()
    },
    upload (imgBase64, imgUrl) {
      const img = {}
      const _this = this
      img.name = imgUrl.substring(imgUrl.lastIndexOf('/') + 1)
      img.type = imgBase64.substring(imgBase64.indexOf(':') + 1, imgBase64.indexOf(';'))
      img.size = 0
      img.base64 = imgBase64
      img.detectType = this.detectType
      _this.$emit('uploadImage', img)
    },
    updateDetectedImage: function (img) { //
      this.disBoardMask(false)
      $('.display-main img').attr('src', img.base64)
    },
    changeDisboardToTask: function (img) { // 显示任务视图
      const _this = this
      _this.activeBox = ''
      _this.updateDetectedImage(img)
    },
    disBoardMask: function (isScan) { // 主显示框是否出现扫描效果
      if (isScan) {
        $('#mainBox div').addClass('wrap-mask')
      } else {
        $('#mainBox div').removeClass('wrap-mask')
      }
    }
  },
  mounted () {
    const _this = this
    $('.example div').addClass('wrap-mask')
    $('#' + _this.activeBox + ' div').removeClass('wrap-mask')
    _this.getBase64($('#' + _this.activeBox + ' img').attr('src'))
  }
}
</script>

<style scoped>
@-webkit-keyframes scrollToDown {
  0% {
    background-position: 0 -20%;
  }
  100% {
    background-position: 0 100%;
  }
}

@keyframes scrollToUp {
  0% {
    background-position: 0 -20%;
  }
  100% {
    background-position: 0 100%;
  }
}

.service-display {
  padding: 10px 0;
}

.inb {
  display: inline-block
}

.display-main {
  width: 70%;
  height: 430px;
  margin-right: 10px;
  margin-bottom: 20px;
  border-radius: 40px;
  background-color: #211004;
  vertical-align: top;
  overflow: hidden;
  position: relative;
}

.dsno {
  display: none;
}

.display-main .scan {
  background-image: url('../../static/imgs/scan.png');
  background-repeat: no-repeat;
  background-size: 100% 20%;
  background-attachment: fixed;
  background-position: center center;
  -webkit-animation: scrollToUp 1s linear infinite;
  animation: scrollToUp 1s linear infinite;
}

.display-minor {
  vertical-align: top;
  width: 25%;
}

.example {
  height: 140px;
  margin-bottom: 10px;
  border-radius: 16px !important;
  background-color: #211004;
  position: relative;
  overflow: hidden;
}

.example img, .display-main img {
  object-fit: contain;
  height: 100%;
  width: 100%;
}

.wrap-mask {
  position: absolute;
  width: 100%;
  height: 100%;
  left: 0;
  top: 0;
  z-index: 1;
  background-color: rgba(0, 0, 0, 0.4);
  border-radius: 16px;
  display: block;
}
</style>
