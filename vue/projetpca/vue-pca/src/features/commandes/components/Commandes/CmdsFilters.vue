<script setup lang="ts">
import type {FiltersInterface, FilterUpdate} from "@/interfaces";
import type {Delai} from "@/interfaces/type";

defineProps<{
  filters:FiltersInterface;
  nbCmds : number;
}>();
const emit = defineEmits<{
  (e:'updateFilter',filterUpdate:FilterUpdate):void;
}>();
</script>

<template>
<div class="p-20 d-flex flex-column">
  <section class="mb-20">
    <h3 class="mb-10">Rechercher</h3>
    <input
        :value="filters.search"
        @input="emit('updateFilter',{search:($event.target as HTMLInputElement).value})"
        type="text"
        placeholder="Rechercher"
    >
  </section>
  <section class="mb-20">
    <h3 class="mb-10">Trier par nbCartes</h3>
    <div
        class="mb-5"
        v-for="nbCartesRange of ([[0,1000],[10,50],[50,100],[100,500],[500,1000]] as [number,number][])"
    >
      <input
          :checked="filters.nbCartesRange[0]===nbCartesRange[0]"
          type="radio"
          @input="emit('updateFilter',{nbCartesRange})"
          name="nbCartesRange"
          :id="nbCartesRange[0].toString()"
      />
      <label :for="nbCartesRange[0].toString()">
        {{
          nbCartesRange[0] === 0
              ? 'Tous les nombres'
              : nbCartesRange[0] === 500
                  ? 'Plus de 500'
                  : `Entre ${nbCartesRange[0]} et ${nbCartesRange[1]}`
        }}

      </label>
    </div>
  </section>
  <section class="mb-20 flex-fill">
    <h3 class="mb-10">Trier par delai</h3>
    <p
      class="delai"
      :class="{selected: filters.delai === delai }"
      v-for="delai in (['all','C','E','F'] as Delai[])"
      @click="emit('updateFilter', {delai})"
    >
      {{ delai }}
    </p>
  </section>
  <small class="mb-5">
    Nombre de résultats
    <strong>{{ nbCmds }}</strong>
  </small>
  <button class="btn btn-danger" @click="emit('updateFilter',{})">
    Supprimer les filtres
  </button>
</div>
</template>

<style scoped lang="scss">
.delai {
  font-size: 14px;
  line-height: 18px;
  cursor: pointer;

  &:hover {
    text-decoration: underline;
  }
}
.selected {
  font-weight: bold;
  text-decoration: underline;
}

</style>