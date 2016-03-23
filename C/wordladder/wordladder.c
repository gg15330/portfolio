#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define ARGS 2

struct word {
  char *string;
};

struct list {
  int visited;
  int depth;
  struct list *next;
  struct list *parent;
  struct word *word;
};

typedef struct word Word;
typedef struct list List;

void check_args(int argc, Word *source, Word *target, List *wlist);
int in_list(char *s, List *wordlist);
int num_different_letters(char *s1, char *s2);
int find_longest_word(FILE *f);

void mark_visited(char *s, List *wlist);
int valid(char *source, char *word);
int new_rungs(List *wlist, int depth);

void read_input(Word *w, int maxwordlen);
List *read_in_file(FILE *f, int maxwordlen);
List *find_in_list(char *source, List *wlist);
List *allocateList(int maxwordlen);
Word *allocateWord(int maxwordlen);
void free_List(List *l);

void get_ladder(char *source, char *target, List *wlist);
void get_next_rung(char *source, List *wlist, int depth);

void print_list(List *l, int depth);
void print_ladder(List *l);


int main(int argc, char *argv[])
{
  Word *source, *target;
  List *start;
  FILE *fp;
  int maxwordlen = 0;

  setbuf(stdout, NULL);
  fp = fopen(argv[1], "r");
  maxwordlen = find_longest_word(fp);
  start = read_in_file(fp, maxwordlen);
  fclose(fp);

  source = allocateWord(maxwordlen);
  target = allocateWord(maxwordlen);

  printf("Source word: ");
  read_input(source, maxwordlen);
  printf("Target word: ");
  read_input(target, maxwordlen);
  check_args(argc, source, target, start);
  mark_visited(source->string, start);
  get_ladder(source->string, target->string, start);

  free(source);
  free(target);
  free_List(start);

  return 0;
}

void check_args(int argc, Word *source, Word *target, List *wlist)
{
  if(argc != ARGS) {
    printf("Incorrect number of arguments.\n");
    exit(1);
  }
  if(strlen(source->string) != strlen(target->string)) {
    printf("Please enter two words of the same length.\n");
    exit(1);
  }
  if(!in_list(source->string, wlist)) {
    printf("Source word not found in word list. Please choose a word that appears in the list.\n");
    exit(1);
  }
  if(!in_list(target->string, wlist)) {
    printf("Target word not found in word list. Please choose a word that appears in the list.\n");
    exit(1);
  }
  if(!strcmp(source->string, target->string)) {
    printf("Please enter two words that are not the same word...\n");
    exit(1);
  }
}

int in_list(char *s, List *wordlist)
{
  while(wordlist->next != NULL) {
    if(!strcmp(s, wordlist->word->string)) {
      return 1;
    }
    wordlist = wordlist->next;
  }
  return 0;
}

int num_different_letters(char *s1, char *s2)
{
  int i = 0;
  int diffs = 0;

  for(i = 0; s1[i] != '\0'; i++) {
    if(s1[i] != s2[i]) {
      diffs++;
    }
  }
  return diffs;
}

int find_longest_word(FILE *f)
{
  int len = 0;
  int count = 0;
  char c;
  while((c = fgetc(f)) != EOF) {
    count++;
    if(c == '\n') {
      if(count > len) {
        len = count;
      }
      count = 0;
    }
  }
  rewind(f);
  return len;
}

void mark_visited(char *s, List *wlist)
{
  while(wlist->next != NULL) {
    if(!strcmp(s, wlist->word->string)) {
      wlist->visited = 1;
    }
    wlist = wlist->next;
  }
}

int valid(char *source, char *word)
{
  if(strlen(source) == strlen(word) && (num_different_letters(source, word) == 1)) {
    return 1;
  }
  return 0;
}

int new_rungs(List *wlist, int depth)
{
  while(wlist->next != NULL) {
    if(depth == wlist->depth) {
      return 1;
    }
    wlist = wlist->next;
  }
  return 0;
}

void read_input(Word *w, int maxwordlen)
{
  char c;
  int count = 0;

  while((c = getchar()) != '\n') {
    if(count >= maxwordlen) {
      printf("Word too long");
      exit(1);
    }
    w->string[count] = c;
    count++;
  }
}

List *read_in_file(FILE *f, int maxwordlen)
{
  List *head;
  List *current;
  int count = 0;
  Word *arr = allocateWord(maxwordlen);

  head = current = allocateList(maxwordlen);
  while(fscanf(f, "%s", arr->string) != EOF) {
    strcpy(current->word->string, arr->string);
    current->next = allocateList(maxwordlen);
    current = current->next;
    count++;
  }
  printf("%d words read\n", count);
  free(arr);
  return head;
}

List *find_in_list(char *source, List *wlist)
{
  List *l;

  while(wlist->next != NULL) {
    if(!strcmp(source, wlist->word->string)) {
      l = wlist;
    }
    wlist = wlist->next;
  }
  return l;
}

List *allocateList(int maxwordlen)
{
  List *l;
  l = (List *)malloc(sizeof(List));

  if(l == NULL) {
    printf("Cannot allocate List.\n");
    exit(1);
  }

  l->word = allocateWord(maxwordlen);
  l->depth = 0;
  l->visited = 0;
  l->next = NULL;
  l->parent = NULL;
  return l;
}

Word *allocateWord(int maxwordlen)
{
  Word *s;
  s = (Word *)malloc(maxwordlen * sizeof(Word));

  if(s == NULL) {
    printf("Cannot allocate Word.\n");
    exit(1);
  }

  s->string = calloc(maxwordlen, sizeof(char));

  return s;
}

void free_List(List *l)
{
  List *current = l;
  while(l != NULL) {
    current = l;
    l = l->next;
    free(current);
  }
}

void get_ladder(char *source, char *target, List *start)
{
  int depth = 0;
  List *current;

  current = start;
  get_next_rung(source, start, depth);
  depth++;
  while(new_rungs(start, depth)) {
    while(current->next != NULL) {
      if(current->depth == depth) {
        if(!strcmp(target, current->word->string)) {
          print_ladder(current);
          return;
        }
        strcpy(source, current->word->string);
        get_next_rung(source, start, depth);
      }
      current = current->next;
    }
    current = start;
    depth++;
  }
  printf("No word ladder found :(\n");
  return;
}

void get_next_rung(char *source, List *wlist, int depth)
{
  List *temp;
  temp = find_in_list(source, wlist);

  while(wlist->next != NULL) {
    if((wlist->visited == 0) && valid(source, wlist->word->string)) {
      wlist->depth = depth + 1;
      wlist->visited = 1;
      wlist->parent = temp;
    }
    wlist = wlist->next;
  }
}

void print_list(List *l, int depth)
{
  int i = 0;
  List *head = l;

  for(i = 1; i <= depth; i++) {
    while(l->next != NULL) {
      if(l->depth == i) {
        printf("String: %s \t\tDepth: %d \tParent: %s\tVisited: %d\n", l->word->string, l->depth, l->parent->word->string, l->visited);
      }
    l = l->next;
    }
    l = head;
    printf("\n");
  }
}

void print_ladder(List *l)
{
  if(l->parent == NULL) {
    printf("%s", l->word->string);
    return;
  }
  print_ladder(l->parent);
  printf(" -> %s", l->word->string);
}
