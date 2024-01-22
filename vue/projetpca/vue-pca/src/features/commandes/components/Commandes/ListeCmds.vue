<script setup lang="ts">

import Commande from "@/features/commandes/components/Commandes/Cmd.vue";
import type {CmdInterface} from "@/interfaces";

defineProps<{cmds : CmdInterface[]}>();

const emit = defineEmits<{
  (e: 'listCartesInBodyCartes',cmdId : number):void
}>();
</script>

<template>
  <div class="grid p-20">
    <Commande
        @list-cartes-in-body-cartes="emit('listCartesInBodyCartes',$event)"
        v-for="cmd of cmds"
        :cmd="cmd"
        :key="cmd.id"
    />

  </div>
</template>

<style scoped lang="scss">
.grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr 1fr;
  grid-auto-rows: 300px;
  gap: 20px;
}
</style>