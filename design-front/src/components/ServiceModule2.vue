<template>
  <div class="service-module">
    <div class="module">
      <div class="module-bottom" :class="{'btn-active':isActive1}"
           @click="switchCopyMove">
        <span class="module-text">Copy-Move篡改检测</span>
      </div>
      <div class="module-bottom" :class="{'btn-active':isActive2}"
           v-on:click="switchSplicing">
        <span class="module-text">拼接篡改检测</span>
      </div>
      <div class="module-bottom" :class="{'btn-active':isActive3}"
           v-on:click="switchGeneral">
        <span class="module-text">通用篡改检测</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ServiceModule2',
  data () {
    return {
      isActive1: true,
      isActive2: false,
      isActive3: false
    }
  },
  methods: {
    switchCopyMove: function () {
      this.isActive1 = true
      this.isActive2 = false
      this.isActive3 = false
      this.$emit('parentFunc', 'copymove')
    },
    switchSplicing: function () {
      this.isActive2 = true
      this.isActive1 = false
      this.isActive3 = false
      this.$emit('parentFunc', 'splicing')
    },
    switchGeneral: function () {
      this.isActive3 = true
      this.isActive1 = false
      this.isActive2 = false
      this.$emit('parentFunc', 'general')
    }
  }
}
</script>

<style scoped>
.service-module {
  padding-bottom: 25px;
}

.module-bottom {
  display: inline-block;
  margin-left: 20px;
  height: 40px;
  border: 1px solid #B5BBCA;
  width: 200px;
  border-radius: 20px;
  color: #4E88BA;
  font-weight: 700;
  text-align: center;
  font-size: 18px;
}

.module-text {
  display: block;
  margin-top: 7px;
}

.module-bottom:hover {
  cursor: pointer;
  background-color: #007FF5;
  color: #fff;
}

.btn-active {
  background-color: #007FF5;
  color: #fff;
}
</style>
