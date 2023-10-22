package inventory;

import entities.WheatPatch;

public class Item {

	public static final String[] IDToName = { "Cupule", "Branche", "Cupule Grillé", "Lapin", "Lapin Cuit", "Champignon",
			"Graine de Blé", "Brin de Blé", "Escargot", "Plat d'Escargot", "Miche de Pain", "Croissant",
			"Soupe de Champignon" };
	public static final String[] IDToNamePlural = { "Cupules", "Branches", "Cupules Grillé", "Lapins", "Lapins Cuit",
			"Champignons", "Graines de Blé", "Brins de Blé", "Escargot", "Plats d'Escargot", "Miches de Pain",
			"Croissants", "Soupes de Champignon" };
	public static final String[] IDToDescription = {
			"Une cupule est de forme ovale et mesure généralement quelques centimètres de long. Lorsque les glands mûrissent, ils tombent des arbres et sont souvent dispersés par les animaux qui les consomment ou les cachent pour se nourrir plus tard. Les glands sont également connus pour être la nourriture préférée des écureuils.",
			"Une branche est une partie d'un arbre qui s'étend à partir du tronc principal. Elle est composée de bois et de feuillage. Elles abritent également de nombreux animaux, tels que les oiseaux, les écureuils et les insectes. Les branches peuvent être coupées pour diverses utilisations, telles que la construction, la production de bois de chauffage ou la création d'œuvres d'art.",
			"Une fois cuit, il subit des transformations qui le rendent plus doux et plus agréable à manger. Les acorns sont généralement rôtis, bouillis ou traités pour éliminer les substances amères qu'ils contiennent naturellement. La saveur des glands cuits varie en fonction de la méthode de cuisson utilisée, mais elle est souvent décrite comme douce et légèrement sucrée.",
			"Un lapin cru est un animal de petite taille souvent utilisé comme source de viande. Il possède une fourrure douce et des oreilles caractéristiques. Elle est souvent utilisée dans de nombreuses cuisines pour préparer une variété de plats, tels que des ragoûts, des grillades ou des terrines.",
			"Un lapin cuit est un plat préparé à partir de la viande de lapin qui a été cuisinée selon différentes méthodes. Après avoir été préparé, le lapin est généralement cuit jusqu'à ce qu'il atteigne une texture tendre et une saveur délicieuse. Le lapin cuit peut être accompagné de divers légumes, herbes et épices pour créer des plats savoureux et nourrissants.",
			"Les champignons crus peuvent être trouvés dans une variété de formes, de tailles et de couleurs. Les champignons crus sont souvent utilisés dans les salades, les marinades ou les trempettes, ajoutant une texture croquante et une saveur terreuse aux plats.",
			"Une graine de blé est le petit embryon d'une plante de blé, qui est utilisée pour cultiver cette céréale. La graine de blé est généralement petite, de forme ovale et de couleur brune. Les graines de blé peuvent également être consommées directement, notamment dans les salades ou les plats à base de céréales.",
			"Un brin de blé est une tige mince et flexible qui fait partie de l'épi de blé. Il est caractérisé par sa couleur jaune paille et sa longueur variable, généralement plusieurs centimètres. Ils sont également souvent associés aux saisons de récolte et aux paysages agricoles.",
			"Un escargot est un petit gastéropode terrestre ayant une coquille en spirale qui lui sert de protection. Il est caractérisé par son corps mou et visqueux, ainsi que par ses tentacules, dont l'un porte ses yeux. Ils sont également souvent utilisés dans la cuisine, notamment en France, où les escargots sont préparés et appréciés comme un mets délicat.",
			"Les escargots sont préparés en utilisant des escargots terrestres qui sont cuits et servis dans leur coquille. Les escargots sont généralement purgés pour éliminer leur mucus et nettoyés avant d'être préparés. C'est un mets délicat et apprécié dans de nombreux restaurants français.",
			"La miche de pain est souvent servie en tranches épaisses, qui peuvent être utilisées pour préparer des sandwichs, accompagner des plats ou être tartinées de beurre ou de confiture. Elle est appréciée pour sa simplicité et sa polyvalence, et est l'un des aliments de base les plus populaires dans de nombreuses cultures à travers le monde.",
			"Les croissants sont souvent dégustés au petit-déjeuner ou à la pause-café. Ils peuvent être servis tels quels, légèrement réchauffés, ou accompagnés de confiture, de beurre ou de pâte à tartiner. Leur texture feuilletée et leur goût délicat en font une pâtisserie appréciée dans le monde entier.",
			"Le ragoût de champignons commence par la préparation des champignons. Différentes variétés de champignons peuvent être utilisées, comme les champignons de Paris, les champignons sauvages ou les champignons portobello. Les champignons sont généralement nettoyés, équeutés et coupés en tranches ou en morceaux, selon les préférences." };
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
	public static String[] missionStatements = { "Pourras-tu me donner une cupule grillé, s'il vous plaît ?",
			"Pourras-tu me trouver une branche?", "Cuisinez-moi un plat d'escargots", "Donne-moi une miche de pain",
			"J'ai envie d'un croissant, pouvez-vous faire un croissant?", "Pouvez-vous me préparer un soupe de champignons?" };

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
