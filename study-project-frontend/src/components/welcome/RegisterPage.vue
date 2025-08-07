<script setup>

import {Lock, User, Message,EditPen} from "@element-plus/icons-vue";
import router from "@/router/index.js";
import {reactive,ref} from "vue";
import {ElMessage} from "element-plus";
import {post} from "@/net/index.js";

const form = reactive({
  username: '',
  password: '',
  password_repeat: '',
  email: '',
  code: ''
})

const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入用户名'));
  } else if (!/^[\u4e00-\u9fa5a-zA-Z]+$/.test(value)) {
    callback(new Error('用户名只能包含中文或英文字符'));
  } else {
    callback();
  }
}

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请在此输入密码'))
  } else if (value !== form.password) {
    callback(new Error("两次输入密码不一致！"))
  } else {
    callback()
  }
}


const rules = {
  username: [
    { validator: validateUsername, trigger: ['blur','change'] },
    { min: 2, max: 8, message: 'Length should be 2 to 8', trigger: ['blur','change'] },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur'},
    { min: 6, max: 16, message: 'Length should be 6 to 16', trigger: ['blur','change'] },
  ],
  password_repeat: [
    { validator: validatePassword, trigger: ['blur','change'] },
  ],
  email: [
    { required: true, message: '请输入邮件地址', trigger: 'blur'},
    {
      type: 'email',
      message: 'Please input correct email address',
      trigger: ['blur', 'change'],
    },
  ],
  code: [
    { required: true, message: '请输入获取的验证码', trigger: 'blur'},
  ]
}

const formRef = ref()
const isEmailValid = ref(false)
const coldTime = ref(0)

const onValidate = (prop,isValid) => {
  if (prop === 'email')
    isEmailValid.value = isValid
}

const register = () => {
  formRef.value.validate((isValid) => {
    if (isValid){
      post('/api/auth/register',{
        username: form.username,
        password: form.password,
        email: form.email,
        code: form.code
      },(message) =>{
        ElMessage.success(message)
        router.push("/")
      })
    }else {
      ElMessage.warning('请填写注册表单内容')
    }
  })
}

const validateEmail = ()=> {
  post('/api/auth/valid-email',{
    email: form.email
  },(message) =>{
    ElMessage.success(message)
    coldTime.value = 60
    setInterval(() => coldTime.value--,1000)
  })
}
</script>

<template>
<div style="text-align: center;margin: 0 20px">
  <div style="margin-top: 100px">
    <div style="font-size: 25px;color: black">注册新用户</div>
    <div style="font-size: 14px;color: gray">欢迎注册我们的注册平台，请在下方填写相关信息</div>
  </div>
  <div style="margin-top: 50px" >
    <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
      <el-form-item prop="username">
        <el-input v-model="form.username" type="text" placeholder="用户名">
          <template #prefix>
            <el-icon><User /></el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input v-model="form.password" type="password" placeholder="密码">
          <template #prefix>
            <el-icon><Lock /></el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password_repeat">
        <el-input v-model="form.password_repeat" type="password" placeholder="重复密码">
          <template #prefix>
            <el-icon><Lock /></el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="email">
        <el-input v-model="form.email" type="text" placeholder="电子邮件地址">
          <template #prefix>
            <el-icon><Message /></el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="code">
        <el-row :gutter="10">
          <el-col :span="17">
            <el-input type="text" :maxlength="6" v-model="form.code" placeholder="输入验证码">
              <template #prefix>
                <el-icon><EditPen /></el-icon>
              </template>
            </el-input>
          </el-col>
          <el-col :span="6">
            <el-button  type="success" @click="validateEmail" :disabled="!isEmailValid || coldTime>0">
              {{coldTime > 0 ? '请稍候'+ coldTime+ '秒' : "获取验证码"}}</el-button>
          </el-col>
        </el-row>
      </el-form-item>
    </el-form>
  </div>
  <div style="margin-top: 10px">

  </div>
  <div style="margin-top: 80px">
    <el-button @click="register" style="width: 270px" type="warning" plain>立即注册</el-button>
  </div>
  <div style="font-size: 14px;line-height: 15px;margin-top: 20px">
    已有帐号?<el-link type="primary" style="translate: 0 -2px" @click="router.push('/')">立即登录</el-link>
  </div>
</div>
</template>

<style scoped>

</style>