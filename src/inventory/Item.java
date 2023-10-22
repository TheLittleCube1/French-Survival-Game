package inventory;

import entities.WheatPatch;

public class Item {

	public static final String[] IDToName = { "Cupule", "Branche", "Cupule Grill�", "Lapin", "Lapin Cuit", "Champignon",
			"Graine de Bl�", "Brin de Bl�", "Escargot", "Plat d'Escargot", "Miche de Pain", "Croissant",
			"Soupe de Champignon" };
	public static final String[] IDToNamePlural = { "Cupules", "Branches", "Cupules Grill�", "Lapins", "Lapins Cuit",
			"Champignons", "Graines de Bl�", "Brins de Bl�", "Escargot", "Plats d'Escargot", "Miches de Pain",
			"Croissants", "Soupes de Champignon" };
	public static final String[] IDToDescription = {
			"Une cupule est de forme ovale et mesure g�n�ralement quelques centim�tres de long. Lorsque les glands m�rissent, ils tombent des arbres et sont souvent dispers�s par les animaux qui les consomment ou les cachent pour se nourrir plus tard. Les glands sont �galement connus pour �tre la nourriture pr�f�r�e des �cureuils.",
			"Une branche est une partie d'un arbre qui s'�tend � partir du tronc principal. Elle est compos�e de bois et de feuillage. Elles abritent �galement de nombreux animaux, tels que les oiseaux, les �cureuils et les insectes. Les branches peuvent �tre coup�es pour diverses utilisations, telles que la construction, la production de bois de chauffage ou la cr�ation d'�uvres d'art.",
			"Une fois cuit, il subit des transformations qui le rendent plus doux et plus agr�able � manger. Les acorns sont g�n�ralement r�tis, bouillis ou trait�s pour �liminer les substances am�res qu'ils contiennent naturellement. La saveur des glands cuits varie en fonction de la m�thode de cuisson utilis�e, mais elle est souvent d�crite comme douce et l�g�rement sucr�e.",
			"Un lapin cru est un animal de petite taille souvent utilis� comme source de viande. Il poss�de une fourrure douce et des oreilles caract�ristiques. Elle est souvent utilis�e dans de nombreuses cuisines pour pr�parer une vari�t� de plats, tels que des rago�ts, des grillades ou des terrines.",
			"Un lapin cuit est un plat pr�par� � partir de la viande de lapin qui a �t� cuisin�e selon diff�rentes m�thodes. Apr�s avoir �t� pr�par�, le lapin est g�n�ralement cuit jusqu'� ce qu'il atteigne une texture tendre et une saveur d�licieuse. Le lapin cuit peut �tre accompagn� de divers l�gumes, herbes et �pices pour cr�er des plats savoureux et nourrissants.",
			"Les champignons crus peuvent �tre trouv�s dans une vari�t� de formes, de tailles et de couleurs. Les champignons crus sont souvent utilis�s dans les salades, les marinades ou les trempettes, ajoutant une texture croquante et une saveur terreuse aux plats.",
			"Une graine de bl� est le petit embryon d'une plante de bl�, qui est utilis�e pour cultiver cette c�r�ale. La graine de bl� est g�n�ralement petite, de forme ovale et de couleur brune. Les graines de bl� peuvent �galement �tre consomm�es directement, notamment dans les salades ou les plats � base de c�r�ales.",
			"Un brin de bl� est une tige mince et flexible qui fait partie de l'�pi de bl�. Il est caract�ris� par sa couleur jaune paille et sa longueur variable, g�n�ralement plusieurs centim�tres. Ils sont �galement souvent associ�s aux saisons de r�colte et aux paysages agricoles.",
			"Un escargot est un petit gast�ropode terrestre ayant une coquille en spirale qui lui sert de protection. Il est caract�ris� par son corps mou et visqueux, ainsi que par ses tentacules, dont l'un porte ses yeux. Ils sont �galement souvent utilis�s dans la cuisine, notamment en France, o� les escargots sont pr�par�s et appr�ci�s comme un mets d�licat.",
			"Les escargots sont pr�par�s en utilisant des escargots terrestres qui sont cuits et servis dans leur coquille. Les escargots sont g�n�ralement purg�s pour �liminer leur mucus et nettoy�s avant d'�tre pr�par�s. C'est un mets d�licat et appr�ci� dans de nombreux restaurants fran�ais.",
			"La miche de pain est souvent servie en tranches �paisses, qui peuvent �tre utilis�es pour pr�parer des sandwichs, accompagner des plats ou �tre tartin�es de beurre ou de confiture. Elle est appr�ci�e pour sa simplicit� et sa polyvalence, et est l'un des aliments de base les plus populaires dans de nombreuses cultures � travers le monde.",
			"Les croissants sont souvent d�gust�s au petit-d�jeuner ou � la pause-caf�. Ils peuvent �tre servis tels quels, l�g�rement r�chauff�s, ou accompagn�s de confiture, de beurre ou de p�te � tartiner. Leur texture feuillet�e et leur go�t d�licat en font une p�tisserie appr�ci�e dans le monde entier.",
			"Le rago�t de champignons commence par la pr�paration des champignons. Diff�rentes vari�t�s de champignons peuvent �tre utilis�es, comme les champignons de Paris, les champignons sauvages ou les champignons portobello. Les champignons sont g�n�ralement nettoy�s, �queut�s et coup�s en tranches ou en morceaux, selon les pr�f�rences." };
	public static final boolean[] COOKABLE = { true, true, true, true, true, true, true, true, true, true, true, true,
			true };
	public static final boolean[] CRAFTABLE = { true, true, true, true, true, true, true, true, true, true, true, true,
			true };
	public static final int ID_ACORN = 0, ID_BRANCH = 1, ID_ROASTED_ACORN = 2, ID_RAW_RABBIT = 3, ID_COOKED_RABBIT = 4,
			ID_MUSHROOM = 5, ID_WHEAT_SEED = 6, ID_WHEAT = 7, ID_SNAIL = 8, ID_ESCARGOT = 9, ID_LOAF_OF_BREAD = 10,
			ID_CROISSANT = 11, ID_MUSHROOM_STEW = 12;

	public static String[] verb = { "", "", "", "", "", "", "Planter", "", "", "", "", "", "" };

	public static boolean[] usable = new boolean[IDToName.length];

	public static int[] mission = { ID_ROASTED_ACORN, ID_BRANCH, ID_COOKED_RABBIT, ID_ESCARGOT,
			ID_LOAF_OF_BREAD, ID_CROISSANT, ID_MUSHROOM_STEW };
	public static String[] missionStatements = { "Pourras-tu me donner une cupule grill�, s'il vous pla�t ?",
			"Pourras-tu me trouver une branche?", "Cuisinez-moi un plat d'escargots", "Donne-moi une miche de pain",
			"J'ai envie d'un croissant, pouvez-vous faire un croissant?", "Pouvez-vous me pr�parer un soupe de champignons?" };

	public static void useItem(int itemID) {

		if (itemID == ID_WHEAT_SEED) {
			System.out.println("Planted wheat seed");
			WheatPatch.plant();
			InventoryUI.closeInventory();
		}

	}

	static {
		for (int i = 0; i < usable.length; i++) {
			usable[i] = !verb[i].equals("");
		}
	}

}
