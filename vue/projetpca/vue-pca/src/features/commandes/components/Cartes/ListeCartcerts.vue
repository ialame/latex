<script setup lang="ts">

import Cartcert from "@/features/commandes/components/Cartes/Cartcert.vue";
import type {CartcertInterface} from "@/interfaces";


const props = defineProps<{
  cartesDansCmd : CartcertInterface[];
}>();
const emit = defineEmits<{
  (e:'removeCartcertFromCmd',cartcertId:number):void;
}>();
</script>

<template>
<div class="d-flex flex-column">
  <Cartcert
    v-for="cartcert of cartesDansCmd"
    :cartcert="cartcert"
    @remove-cartcert-from-cmd="emit('removeCartcertFromCmd',$event)"
  />

</div>
</template>

<style scoped lang="scss">

</style>