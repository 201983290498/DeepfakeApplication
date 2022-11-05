<template >
  <div id="app" style="background-color:#F8F8F9 !important">
    <nav class="navbar navbar-fixed-top content-mg">
      <div class="container-fluid">
        <!--Logo加上商标名-->
        <div class="navbar-header  font-weight-bolder">
          <div class="navbar-brand">
            <img alt="logo" src="../static/imgs/logo.png" class="logo-pic">
            DeepFake Detector
          </div>
        </div>
        <!--功能栏-->
        <div class="collapse navbar-collapse ">
          <!--主要功能-->
          <ul class="nav navbar-nav mg-auto">
            <li>
              <router-link to="/" active-class="func-active">deepfake篡改检测</router-link>
            </li>
            <li class="active">
              <router-link to="/NormalDetector" active-class="func-active">普通篡改检测</router-link>
            </li>
          </ul>
          <!--用户信息栏-->
          <ul class="nav navbar-nav ml-auto">
            <li class="active">
              <a><img class="rounded-circle mr-2" src="../static/imgs/av.png" style="width:30px"><span class="align-middle" >{{userStatus}}</span></a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
<!--todo 需要修改margin为原来的1半-->
    <router-view style="margin-top: 70px; text-align: center" class="content-mg content-pd"></router-view>
    <div class="page-bottom"></div>
  </div>
</template>

<script>
export default {
// name 属性指向的是当前组件的名称(建议:每个单词的首字母大写)
  name: 'App',
  data () {
    return {
      userStatus: 'Login in'
    }
  }
}
</script>

<style lang="less">
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  s-webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
.content-mg{
  margin-left: 10%;
  margin-right: 10%;
}
.content-pd{
  padding-left: 10%;
  padding-right: 10%;
  text-align: left;
  padding-top: 20px;
}
.logo-pic{
  float: left;
  margin-top: -6%;
  margin-right: 1rem;
}
.navbar{
  padding: 0.5rem 2rem;
  align-items: center;
  background-color:#fff !important;
  border: 1px solid #f1f1f1 !important;
  a {
    font-weight: bold;
    color: #2c3e50;

    &.router-link-exact-active {
      color: #42b983;
      background-color: #eee;
    }
  }
}
.mg-auto{
  position: relative;
  left: auto !important;
}
.navbar-brand{
  font-weight: bold;
  color: #2c3e50;
}
.ml-auto{
  float: right !important;
}
.rounded-circle{
  border-radius: 50%;
}
.mr-2{
  margin-right: 0.66rem;
}
.page-bottom{
  width: 100%;
  height: 50px;
}
</style>
