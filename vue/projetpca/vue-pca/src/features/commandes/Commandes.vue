<script setup lang="ts">
import Cmds from "@/features/commandes/components/Commandes/Cmds.vue";
import Cartcerts from "@/features/commandes/components/Cartes/Cartcerts.vue";
import dataCmd from '@/data/cmd';
import dataCartcert from '@/data/cartcert';

import {computed, reactive} from "vue";
import type {CmdInterface, FiltersInterface, CartcertInterface, FilterUpdate} from "@/interfaces";
import {DEFAULT_FILTERS} from "@/features/commandes/data/filters";



const state = reactive<{
  cmds : CmdInterface[];
  cartcerts : CartcertInterface[];
  cartesDansCmd : CartcertInterface[];
  filters : FiltersInterface;
}>({
  cmds: dataCmd,
  cartcerts : dataCartcert,
  cartesDansCmd :[],
  filters: DEFAULT_FILTERS,
});
function listCartesInBodyCartes(cmdId : number): void{
  state.cartesDansCmd = state.cartcerts.filter((cc)=>cc.order === cmdId);
}
function removeCartcertFromCmd(cartcertId:number):void{
  state.cartesDansCmd= state.cartesDansCmd.filter((cc)=>cc.id !== cartcertId);
}

function updateFilter(filterUpdate: FilterUpdate) {
  if (filterUpdate.search !== undefined) {
    state.filters.search = filterUpdate.search;
  } else if (filterUpdate.nbCartesRange) {
    state.filters.nbCartesRange = filterUpdate.nbCartesRange;
  } else if (filterUpdate.delai) {
    state.filters.delai = filterUpdate.delai;
  } else {
    state.filters = { ...DEFAULT_FILTERS };
  }
}

const cmdEmpty = computed(()=>state.cartesDansCmd.length===0);

const filteredCmds = computed(() => {
  return state.cmds.filter((cmd) => {
    if (
        cmd.status
            .toLocaleLowerCase()
            .startsWith(state.filters.search.toLocaleLowerCase()) &&
        cmd.nbCards >= state.filters.nbCartesRange[0] &&
        cmd.nbCards <= state.filters.nbCartesRange[1] &&
        (cmd.delai === state.filters.delai ||
            state.filters.delai === 'all')
    ) {
      return true;
    } else {
      return false;
    }
  });
});

</script>

<template>
  <div class="commandes-container" :class="{'grid-empty' : cmdEmpty}" >

    <Cmds
        @update-filter="updateFilter"
        :cmds="filteredCmds"
        :filters="state.filters"
        @list-cartes-in-body-cartes="listCartesInBodyCartes"
        class="cmds"></Cmds>
    <Cartcerts
        v-if="!cmdEmpty"
        :cartesDansCmd="state.cartesDansCmd"
        class="cartcerts"
        @remove-cartcert-from-cmd="removeCartcertFromCmd"
    />

  </div>
</template>

<style scoped lang="scss">
.commandes-container{
  display: grid;
  grid-template-columns: 75% 25%;
}
.grid-empty {
  grid-template-columns: 100%;
}

.cartcerts{
  background-color: white;
  border-left: var(--border);
}
</style>