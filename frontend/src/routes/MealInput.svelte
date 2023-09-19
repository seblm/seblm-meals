<script lang="ts">
  import type { SearchSuggestions } from '$lib/model/WeekMeals';
  import { createEventDispatcher } from 'svelte';

  export let value: string;
  export let title: string;
  export let suggestions: SearchSuggestions[] | null;
  $: showSuggestions = suggestions && suggestions.length;


  const dispatch = createEventDispatcher();
  function handleInputChange(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    dispatch('valueChange', { value });
  }

  function handleEnterPress(keyEvent) {
    if(keyEvent.code === 'Enter') {
      dispatch('enterPressed');
    }
  }

  function onSelectSuggestion(description: string) {
    dispatch('selectSuggestion', {description});
  }
</script>

<input
    type="text"
    value={value}
    title={title}
    on:input={handleInputChange}
    on:keypress={handleEnterPress}
    on:focus={handleInputChange}
/>
{#if showSuggestions}
  <ul
      class="suggestions"
      class:suggestions-visible={showSuggestions}
  >
    {#each suggestions ?? [] as suggestion}
      <li on:click={() => onSelectSuggestion(suggestion.description)}>
        <span title={suggestion.description}>{@html suggestion.descriptionLabel} </span>
        <span> ({suggestion.count})</span>
        <div class="spacer" />
        <span>{suggestion.lastused} jours</span>
      </li>
    {/each}
  </ul>
{/if}

<style lang="scss">
  input[type='text'] {
    width: 100%;
    margin: auto 1rem;
    padding: 1rem;
    border: none;
    border-radius: 5px;
    box-shadow: inset 2px 3px 6px rgba(0, 0, 0, 0);
    transition: 0.2s ease-in;
    overflow: hidden;
    text-overflow: ellipsis;
    &:focus-visible {
      box-shadow: inset 2px 3px 6px var(--color-bg-0);
    }
  }
  .suggestions {
    display: none;
    position: absolute;
    left: 0;
    top: 100%;
    width: 100%;
    background: #fff;
    border-radius: 5px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2), 0 2px 6px rgba(0, 0, 0, 0.1);
    z-index: 9;
    padding: 0;
    max-height: 300px;
    overflow-y: auto;
    transform: translateY(-2.5rem);
    &-visible {
      display: block;
    }
    li {
      list-style: none;
      margin: 0;
      padding: 0.5rem 1rem;
      cursor: pointer;
      transition: 0.2s ease-in;
      display: flex;
      span {
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        &:first-child {
          max-width: 200px;
        }
        &:not(:last-child) {
          margin-right: 5px;
        }
      }
      &:hover {
        background: var(--color-bg-2);
      }
    }
  }
</style>