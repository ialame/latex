<script setup lang="ts">

import type {CartcertInterface} from "@/interfaces";

defineProps<{
  cartcert: CartcertInterface;
}>();

const emit = defineEmits<{
  (e:'removeCartcertFromCmd',cartcertId:number):void;
}>();

</script>

<template>
  <div class="mb-10 p-10 d-flex flex-row align-items-center cartecert">
    <strong class="mr-10">{{cartcert.name}}</strong>
    <span class="flex-fill mr-10"> {{cartcert.label}}</span>
    <span class="mr-10">{{cartcert.idPCA}}</span>
    <button
        class="btn btn-danger"
        @click="emit('removeCartcertFromCmd',cartcert.id)"
        >
      Supprimer
    </button>
  </div>
</template>

<style lang="scss" scoped>
.cartecert {
  border: var(--border);
  border-radius: var(--border-radius);
  background-color: var(--gray-1);
}
</style>
