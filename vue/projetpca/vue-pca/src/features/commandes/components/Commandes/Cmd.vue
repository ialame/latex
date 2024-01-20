<script setup lang="ts">

import type {CmdInterface} from "@/interfaces";

defineProps<{cmd:CmdInterface}>();

const emit = defineEmits<{
  (e: 'listCartesInBodyCartes',cmdId : number):void
}>();
</script>

<template>
  <div class="product d-flex flex-column">
    <div class="product-image"></div>
    <div class="p-10 d-flex flex-column">
      <h4>Commande N°: {{ cmd.num }}</h4>
      <p>Delai: {{cmd.delai}}</p>
      <p>Nombre des cartes: {{cmd.nbCards}}</p>
      <p>Prix: {{cmd.prix}}</p>
      <p>Status: {{cmd.status}}</p>
      <div class="d-flex flex-row align-items-center">
        <strong class="flex-fill">Date limite</strong>
        <button
            @click="emit('listCartesInBodyCartes',cmd.id)"
            class="btn btn-primary">
          Ouvrire la commande
        </button>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.product {
  background-color: #ffffff;
  border: var(--border);
  border-radius: var(--border-radius);
  &-image {
    border-top-right-radius: var(--border-radius);
    border-top-left-radius: var(--border-radius);
    background-size: cover;
    background-position: center;
    height: 250px;
  }
}
</style>
