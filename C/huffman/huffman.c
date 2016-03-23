#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <ctype.h>

#define NUMARGS 2
#define TEXTFILE argv[1]
#define NUMCHARS 128
#define STRSIZE 3000
#define EMPTYNODE -1
#define MAXCODELENGTH 25
#define ASCIISIZE 8

struct node {
  int character;
  int count;
  int codelen;
  char *code;
  struct node *left;
  struct node *right;
  struct node *next;
};

typedef struct node Node;

void checkArgs(int argc);
void initLeafArray(Node leafarray[]);
int countCompare(const void *a, const void *b);
int charCompare(const void *a, const void *b);
int calculateBytes(Node leafarray[]);
char *computeCode(Node *t, char c);
void assignCodes(Node leafarray[], Node *root);
void sortedInsert(Node *start, Node *new);

void readInFile(FILE *f, Node leafarray[]);
Node *buildLeafList(Node leafarray[]);
Node *buildHuffTree(Node *start);

Node *createParent(Node *left, Node *right);
Node *allocateNode();
char *allocateString();
void freeList(Node *start);
void freeStrings(Node leafarray[]);

void printList(Node *current);
void printArray(Node leafarray[], int totalbytes);
char *PrintTree(Node *t);

int main(int argc, char *argv[])
{
  FILE *fp;
  Node leafarray[NUMCHARS];
  Node *start, *root;
  int totalbytes = 0;

  checkArgs(argc);
  initLeafArray(leafarray);
  fp = fopen(TEXTFILE, "r");
  readInFile(fp, leafarray);
  fclose(fp);
  start = buildLeafList(leafarray);
  root = buildHuffTree(start);
  assignCodes(leafarray, root);
  totalbytes = calculateBytes(leafarray);
  printArray(leafarray, totalbytes);
  freeList(start);
  freeStrings(leafarray);

  return 0;
}

void checkArgs(int argc)
{
  if(argc != NUMARGS) {
    fprintf(stderr, "Incorrect number of arguments.\n");
    exit(1);
  }
}

void initLeafArray(Node leafarray[])
{
  int i = 0;

  for(i = 0; i < NUMCHARS; i++) {
    leafarray[i].character = i;
    leafarray[i].count = 0;
    leafarray[i].codelen = 0;
    leafarray[i].left = NULL;
    leafarray[i].right = NULL;
    leafarray[i].next = NULL;
  }
}

int charCompare(const void *a, const void *b)
{
  Node *ia = (Node *)a;
  Node *ib = (Node *)b;
  return ((ia)->character - (ib)->character);
}

int countCompare(const void *a, const void *b)
{
  Node *ia = (Node *)a;
  Node *ib = (Node *)b;
  return ((ia)->count - (ib)->count);
}

int calculateBytes(Node leafarray[])
{
  int i = 0;
  int sum = 0;

  for(i = 0; i < NUMCHARS; i++) {
    sum += (leafarray[i].count * leafarray[i].codelen);
  }
  sum /= ASCIISIZE;
  return sum;
}

char *computeCode(Node *t, char c)
{
  /*not working*/
  char *str;

  str = (char *)calloc(STRSIZE, sizeof(char));
  assert(str!=NULL);

  if(!t->left && !t->right) {
    strcpy(str, "not found");
  }

  else {
    if(t->left) {
      sprintf(str, "0%s", computeCode(t->left, c));
    }
    if(t->right) {
      sprintf(str, "1%s", computeCode(t->right, c));
    }
  }
  return str;
}

void assignCodes(Node leafarray[], Node *root)
{
  int i = 0;
  for(i = 0; i < NUMCHARS; i++) {
    leafarray[i].code = allocateString();
    /*computeCode function does not work correctly*/
    /*leafarray[i].code = computeCode(root, leafarray[i].character);*/

    /*placeholder huffman code*/
    strcpy(leafarray[i].code, "01010101");
    leafarray[i].codelen = strlen(leafarray[i].code);
  }
}

void sortedInsert(Node *current, Node *new)
{
  while(current->next && (new->count > current->count)) {
    current = current->next;
  }

  new->next = current->next;
  current->next = new;
}

void readInFile(FILE *f, Node leafarray[])
{
  int i = 0;
  char c;

  if(!f) {
    fprintf(stderr, "Could not read in file.\n");
    exit(1);
  }

  while((c = fgetc(f)) != EOF) {
    for(i = 0; i < NUMCHARS; i++) {
      if((leafarray[i].character == c)) {
        leafarray[i].count++;
      }
    }
  }
}

Node *buildLeafList(Node leafarray[])
{
  int i = 0;
  Node *head, *current;

  /*create first element in list (character initialised to EMPTYNODE)*/
  current = head = allocateNode();
  qsort(leafarray, NUMCHARS, sizeof(Node), countCompare);
  /*create a sorted linked list only from characters which appear in file*/
  for(i = 0; i < NUMCHARS; i++) {
    if(leafarray[i].count > 0) {
      /*if character is not set (i.e. 1 element in list)*/
      if(current->character == EMPTYNODE) {
        current->character = leafarray[i].character;
        current->count = leafarray[i].count;
      }
      else {
        current->next = allocateNode();
        current->next->character = leafarray[i].character;
        current->next->count = leafarray[i].count;
        current = current->next;
      }
    }
  }
  return head;
}

Node *buildHuffTree(Node *start)
{
  Node *parent;
  Node *current = start;

  /*if only one Node in list*/
  if(!current->next) {
    return current;
  }

  while(current->next) {
    parent = createParent(current, current->next);
    sortedInsert(start, parent);
    current = current->next->next;
  }
  return parent;
}

Node *createParent(Node *left, Node *right)
{
  Node *parent = allocateNode();
  parent->left = left;
  parent->right = right;
  parent->count = (left->count + right->count);
  return parent;
}

Node *allocateNode()
{
  Node *n = (Node *)malloc(sizeof(Node));

  if(!n) {
    fprintf(stderr, "Could not allocate Node.\n");
    exit(1);
  }

  n->character = EMPTYNODE;
  n->count = 0;
  n->codelen = 0;
  n->left = NULL;
  n->right = NULL;
  n->next = NULL;
  return n;
}

char *allocateString()
{
  char *str = (char *)calloc(MAXCODELENGTH, sizeof(char));

  if(!str) {
    fprintf(stderr, "Could not allocate string.\n");
    exit(1);
  }
  return str;
}

void freeList(Node *start)
{
  Node *temp;
  Node *n = start;

  while(n) {
    temp = n;
    n = n->next;
    free(temp);
  }
  start = NULL;
}

void freeStrings(Node leafarray[])
{
  int i = 0;

  for(i = 0; i < NUMCHARS; i++) {
    free(leafarray[i].code);
  }
}

void printList(Node *current)
{
  while(current) {
    if(current->character != EMPTYNODE) {
      printf("%3d: %5d\n", current->character, current->count);
    }
    else {
      printf("  #: %5d\n", current->count);
    }
    current = current->next;
  }
}

void printArray(Node leafarray[], int totalbytes)
{
  int i = 0;

  qsort(leafarray, NUMCHARS, sizeof(Node), charCompare);
  for(i = 0; i < NUMCHARS; i++) {
    if(iscntrl(leafarray[i].character)) {
      printf("%3d : %15s ( %5d * %5d)\n", leafarray[i].character,
                                          leafarray[i].code,
                                          leafarray[i].codelen,
                                          leafarray[i].count);
    }
    else {
      printf("'%c' : %15s ( %5d * %5d)\n", leafarray[i].character,
                                           leafarray[i].code,
                                           leafarray[i].codelen,
                                           leafarray[i].count);
    }
  }
  printf("%d bytes\n", totalbytes);
}

/*copied from lecture notes - demonstrates that tree is built correctly*/
char *PrintTree(Node *t)
{
  char *str;

  str = calloc(STRSIZE, sizeof(char));
  assert(str != NULL);

  if(t == NULL){
    strcpy(str, "*");
    return str;
  }
  sprintf(str, "%c%d(%s)(%s)", t->character, t->count, PrintTree(t->left), PrintTree(t->right));
  return str;
}
