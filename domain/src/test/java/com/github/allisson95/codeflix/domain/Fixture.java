package com.github.allisson95.codeflix.domain;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.List;
import static io.vavr.API.Match;

import java.time.Year;
import java.util.Set;

import com.github.allisson95.codeflix.domain.castmember.CastMember;
import com.github.allisson95.codeflix.domain.castmember.CastMemberType;
import com.github.allisson95.codeflix.domain.category.Category;
import com.github.allisson95.codeflix.domain.genre.Genre;
import com.github.allisson95.codeflix.domain.video.Rating;
import com.github.allisson95.codeflix.domain.video.Resource;
import com.github.allisson95.codeflix.domain.video.Video;

import net.datafaker.Faker;

public final class Fixture {

    private static final Faker FAKER = new Faker();

    private Fixture() throws IllegalAccessException {
        throw new IllegalAccessException("Cannot construct a instance of Fixture");
    }

    public static boolean bool() {
        return FAKER.bool().bool();
    }

    public static double duration() {
        return FAKER.options().option(58.0, 120.0, 78.3, 103.9);
    }

    public static String name() {
        return FAKER.name().fullName();
    }

    public static String title() {
        return FAKER.options().option(
            "Five Nights at Freddy's - O Pesadelo Sem Fim",
            "A Queda da Casa de Usher",
            "Bodies"
        );
    }

    public static Integer year() {
        return FAKER.random().nextInt(2020, 2030);
    }

    public static class Categories {

        private static final Category FILMES = Category.newCategory("Filmes", "A categoria mais assistida", true);
        private static final Category SERIES = Category.newCategory("Series", "Series mais assistidas", true);
        private static final Category DOCUMENTARIOS = Category.newCategory("Documentários", "Os melhores documentários de 2023", true);

        private Categories() throws IllegalAccessException {
            throw new IllegalAccessException("Cannot construct a instance of Fixture.Categories");
        }

        public static Category filmes() {
            return Category.with(FILMES);
        }

        public static Category series() {
            return Category.with(SERIES);
        }

        public static Category documentarios() {
            return Category.with(DOCUMENTARIOS);
        }

        public static Category random() {
            final var category = FAKER.options().option(
                    FILMES,
                    DOCUMENTARIOS,
                    SERIES);

            return Category.with(category);
        }

    }

    public static class CastMembers {

        private static final CastMember NICOLAS_CAGE = CastMember.newMember("Nicolas Cage", CastMemberType.ACTOR);
        private static final CastMember CLINT_EASTWOOD = CastMember.newMember("Clint Eastwood", CastMemberType.ACTOR);
        private static final CastMember MORGAN_FREEMAN = CastMember.newMember("Morgan Freeman", CastMemberType.ACTOR);
        private static final CastMember STEVEN_SPIELBERG = CastMember.newMember("Steven Spielberg", CastMemberType.DIRECTOR);
        private static final CastMember STANLEY_KUBRICK = CastMember.newMember("Stanley Kubrick", CastMemberType.DIRECTOR);
        private static final CastMember QUENTIN_TARANTINO = CastMember.newMember("Quentin Tarantino", CastMemberType.DIRECTOR);

        private CastMembers() throws IllegalAccessException {
            throw new IllegalAccessException("Cannot construct a instance of Fixture.CastMembers");
        }

        public static CastMemberType type() {
            return FAKER.options().option(CastMemberType.values());
        }

        public static CastMember nicolasCage() {
            return CastMember.with(NICOLAS_CAGE);
        }

        public static CastMember clintEastwood() {
            return CastMember.with(CLINT_EASTWOOD);
        }

        public static CastMember morganFreeman() {
            return CastMember.with(MORGAN_FREEMAN);
        }

        public static CastMember stevenSpielberg() {
            return CastMember.with(STEVEN_SPIELBERG);
        }

        public static CastMember stanleyKubrick() {
            return CastMember.with(STANLEY_KUBRICK);
        }

        public static CastMember quentinTarantino() {
            return CastMember.with(QUENTIN_TARANTINO);
        }

        public static CastMember random() {
            final var castMember = FAKER.options().option(
                    NICOLAS_CAGE,
                    CLINT_EASTWOOD,
                    MORGAN_FREEMAN,
                    STEVEN_SPIELBERG,
                    STANLEY_KUBRICK,
                    QUENTIN_TARANTINO);

            return CastMember.with(castMember);
        }

    }

    public static class Genres {

        private static final Genre ACAO = Genre.newGenre("Ação", true);
        private static final Genre FICCAO_CIENTIFICA = Genre.newGenre("Ficção Científica", true);
        private static final Genre TERROR = Genre.newGenre("Terror", true);

        private Genres() throws IllegalAccessException {
            throw new IllegalAccessException("Cannot construct a instance of Fixture.Genres");
        }

        public static Genre acao() {
            return Genre.with(ACAO);
        }

        public static Genre ficcaoCientifica() {
            return Genre.with(FICCAO_CIENTIFICA);
        }

        public static Genre terror() {
            return Genre.with(TERROR);
        }

        public static Genre random() {
            final var genre = FAKER.options().option(
                    ACAO,
                    FICCAO_CIENTIFICA,
                    TERROR);

            return Genre.with(genre);
        }

    }

    public static class Videos {

        public static Video random() {
            return Video.newVideo(
                    Fixture.title(),
                    description(),
                    Year.of(Fixture.year()),
                    Fixture.duration(),
                    rating(),
                    Fixture.bool(),
                    Fixture.bool(),
                    Set.of(Fixture.Categories.random().getId()),
                    Set.of(Fixture.Genres.random().getId()),
                    Set.of(Fixture.CastMembers.random().getId()));
        }

        private Videos() throws IllegalAccessException {
            throw new IllegalAccessException("Cannot construct a instance of Fixture.Videos");
        }

        public static String description() {
            return FAKER.options().option(
                "Five Nights At Freddy's - O Pesadelo Sem Fim é a primeira adaptação cinematográfica da famosa franquia homônima de jogos lançada em 2014 e criada por Scott Cawthon. Dirigido por Emma Tammi (Terra Assombrada, Fair Chase), a história se passa em um restaurante familiar tipicamente americano chamado Freddy Fazbear's Pizza, que está atualmente desativado, e acompanha Mike Schmidt (Josh Hutcherson), um jovem que está passando por alguns problemas financeiros. Felizmente, ele vê a resposta para seus problemas ao ser contratado para trabalhar como o vigia noturno da pizzaria. Criado por Henry Emily e William Afton, o lugar costumava ser muito famoso por seus característicos robôs animados, que eram o rosto do local e faziam a festa das crianças durante o dia. Porém, quando o sol se põe e a escuridão da noite chega, um segredo obscuro e mortal é revelado: os bonecos animatrônicos ganham vida, transformando-se em assassinos psicopatas e partindo em uma violenta matança.",
                "Baseada na obra de Edgar Allan Poe com o mesmo nome e criada por Mike Flanagan, a minissérie A Queda da Casa de Usher acompanha a história dos irmãos Roderick (Bruce Greenwood) e Madeline Usher (Mary McDonnell), os herdeiros da dinastia Usher. Conhecidos por transformar a Farmacêutica Fortunato em um império de privilégios, luxo e poder, os irmãos agora se encontram isolados e extremamente doentes. Em uma casa que um dia já habitou o sucesso, agora apenas resta um ar de podridão e abandono. Presos nas mãos de uma misteriosa mulher de seu passado, a dinastia  Usher terá que enfrentar os segredos que vêm à tona quando uma morte marca o início de uma grande tragédia.",
                "Em Corpos, quatro detetives de períodos históricos completamente diferentes resolvem o mesmo assassinato em suas respectivas linhas temporais. Na minissérie baseada na graphic novel homônima de Si Spencer, o mesmo cadáver é encontrado em um beco londrino por quatro detetives em épocas distintas ao longo de 150 anos. Ofuscado pelo terror de Jack, O Estripador em 1890, em meio ao caos dos bombardeios alemães da Segunda Guerra Mundial e, mais tarde, encontrado por uma policial em 2023 e num mundo pós-apocalíptico em 2053, o mistério se repete em diferentes contextos e condições."
            );
        }

        public static Rating rating() {
            return FAKER.options().option(Rating.values());
        }

        public static Resource resource(final Resource.Type type) {
            final String contentType = Match(type).of(
                Case($(List(Resource.Type.VIDEO, Resource.Type.TRAILER)::contains), "video/mp4"),
                Case($(), "image/jpg")
            );

            final byte[] content = "content".getBytes();

            return Resource.of(content, contentType, type.name().toLowerCase(), type);
        }

    }

}
