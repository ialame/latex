<script setup lang="ts">
import type {CmdInterface, FiltersInterface, FilterUpdate} from "@/interfaces";
import ListeCommandes from "@/features/commandes/components/Commandes/ListeCmds.vue";
import CmdsFilters from "@/features/commandes/components/Commandes/CmdsFilters.vue";

defineProps<{
  cmds :CmdInterface[];
  filters:FiltersInterface;
}>();

const emit = defineEmits<{
  (e: 'listCartesInBodyCartes',cmdId : number):void;
  (e:'updateFilter',updateFilter:FilterUpdate):void;
}>();
</script>

<template>

<div class="d-flex flex-row">
  <CmdsFilters
    :filters="filters"
    :nb-cmds="cmds.length"
    @update-filter="emit('updateFilter',$event)"
    class="cmds-filter"
    />
  <ListeCommandes
      @list-cartes-in-body-cartes="emit('listCartesInBodyCartes',$event)"
      :cmds="cmds"
  />
</div>
</template>

<style scoped lang="scss">
.cmds-filter{
  flex: 0 0 200px;
}
</style>