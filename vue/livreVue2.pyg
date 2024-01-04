<template>
  <div class="container">
    <div class="p-20">
      <h3 class="mb-10">Formulaire</h3>
      <form @submit="mySubmit">
        <input
          ref="name"
          v-model="nameValue"
          class="mr-10"
          type="text"
          placeholder="Prénom"
        />
        <input
          v-model="emailValue"
          class="mr-10"
          type="text"
          placeholder="Email"
        />
        <button class="btn btn-primary">Sauvegarder</button>
      </form>
    </div>
    <div class="p-20">
      <h3>Liste des utilisateurs</h3>
      <ul>
        <li
          @click="state.selectedUser = user"
          class="mb-10 d-flex"
          v-for="user in state.users"
        >
          <span class="mr-10 flex-fill"
            >{{ user.name }} - {{ user.email }}</span
          >
          <button
            @click.stop="deleteUser(user._id)"
            type="button"
            class="btn btn-danger"
          >
            Supprimer
          </button>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useForm, useField } from 'vee-validate';
import { reactive, ref, watch, onMounted } from 'vue';

interface User {
  name: string;
  email: string;
  createdAt?: string;
  _id?: string;
}

const state = reactive<{
  users: User[];
  selectedUser: User | null;
}>({
  users: [],
  selectedUser: null,
});
const name = ref<HTMLInputElement | null>(null);

onMounted(() => name.value?.focus());

const { handleSubmit, resetForm } = useForm();

const mySubmit = handleSubmit(async (value) => {
  try {
    if (state.selectedUser) {
      const response = await fetch(
        `https://restapi.fr/api/vueusers?id=${state.selectedUser._id}`,
        {
          method: 'PATCH',
          body: JSON.stringify(value),
          headers: {
            'Content-Type': 'application/json',
          },
        }
      );
      const user: User = await response.json();
      state.users = state.users.map((u) => (u._id === user._id ? user : u));
      state.selectedUser = null;
    } else {
      const response = await fetch('https://restapi.fr/api/vueusers', {
        method: 'POST',
        body: JSON.stringify(value),
        headers: {
          'Content-Type': 'application/json',
        },
      });
      const user: User = await response.json();
      state.users.push(user);
    }
    resetForm();
    name.value?.focus();
  } catch (err) {
    console.error(err);
  }
});

const { value: emailValue } = useField('email');
const { value: nameValue } = useField('name');

async function fetchUsers() {
  try {
    const response = await fetch('https://restapi.fr/api/vueusers');
    const users: User | User[] = await response.json();
    if (users) {
      state.users = Array.isArray(users) ? users : [users];
    }
  } catch (err) {
    console.error(err);
  }
}

fetchUsers();

async function deleteUser(userId?: string) {
  try {
    if (userId) {
      await fetch(`https://restapi.fr/api/vueusers?id=${userId}`, {
        method: 'DELETE',
      });
      state.users = state.users.filter((user) => user._id !== userId);
    }
  } catch (err) {
    console.error(err);
  }
}

watch(
  () => state.selectedUser,
  (user: User | null) => {
    if (user) {
      nameValue.value = user.name;
      emailValue.value = user.email;
    } else {
      nameValue.value = '';
      emailValue.value = '';
    }
  }
);
</script>

<style lang="scss">
@import './assets/scss/base.scss';
</style>
