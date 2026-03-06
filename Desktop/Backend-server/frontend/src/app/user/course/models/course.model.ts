export interface ContenuPedagogique {
  idContent?: number;
  titleC: string;
  duration: number;
  contentType: string;
  cours?: { id: number };
}

export interface Cours {
  id?: number;
  idProfessor: number;
  title: string;
  description: string;
  content: string;
  contenus?: ContenuPedagogique[];
}
