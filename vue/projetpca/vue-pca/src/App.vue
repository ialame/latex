<script setup lang="ts">
import TheHeader from './components/Header.vue'
import TheFooter from './components/Footer.vue'
import Commandes from "@/features/commandes/Commandes.vue";


</script>

<template>
  <div class="app-container">
    <TheHeader class="header" />
    <div class="app-container"> <Component :is="Commandes" /> </div>
    <TheFooter class="footer"></TheFooter>
  </div>
</template>

<style lang="scss">
@use "assets/scss/base" as *;
@use "assets/scss/debug" as *;
.app-container {
  height: 100vh;
  display: grid;
  grid-template-areas: 'header' 'app-content' 'footer';
  grid-template-rows: 48px auto 48px;
}

.header {  grid-area: header;}
.app-content {  grid-area: app-content;}
.footer {  grid-area: footer;}
</style>
