<script setup lang="ts">

import ListeCartcerts from "@/features/commandes/components/Cartes/ListeCartcerts.vue";

import type {CartcertInterface} from "@/interfaces";
import {computed} from "vue";
import cartcert from "@/data/cartcert";

const props = defineProps<{
  cartesDansCmd : CartcertInterface[];
}>();
const total = computed(()=>
  props.cartesDansCmd.reduce((acc,cartcert)=>acc+1,0)
);
const emit = defineEmits<{
  (e:'removeCartcertFromCmd',cartcertId:number):void;
}>();
</script>

<template>
  <div class="p-20">
    <h2 class="mb-10">Cartes</h2>
    <ListeCartcerts
      class="flex-fill"
      :cartesDansCmd="cartesDansCmd"
      @remove-cartcert-from-cmd="emit('removeCartcertFromCmd',$event)"
    />
    <button class="btn btn-success">Commander {{ total }}</button>
  </div>
</template>

<style scoped lang="scss">

</style>