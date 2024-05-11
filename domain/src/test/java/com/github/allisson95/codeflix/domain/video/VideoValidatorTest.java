package com.github.allisson95.codeflix.domain.video;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Year;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.github.allisson95.codeflix.domain.UnitTest;
import com.github.allisson95.codeflix.domain.castmember.CastMemberID;
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.exceptions.DomainException;
import com.github.allisson95.codeflix.domain.genre.GenreID;
import com.github.allisson95.codeflix.domain.validation.handler.ThrowsValidationHandler;

class VideoValidatorTest extends UnitTest {

    @Test
    void Given_NullTitle_When_CallsValidate_Should_ReceiveError() {
        final String expectedTitle = null;
        final var expectedDescription = """
                Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniôes pessoais.
                Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                Para acessar todas as aulas, lives e desafios, acesse:
                https://imersao.fullcycle.com.br/
                """;
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.0;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var expcetedErrorCount = 1;
        final var expectedErrorMessage = "'title' should not be null";

        final var actualVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedRating,
                expectedOpened,
                expectedPublished,
                expectedCategories,
                expectedGenres,
                expectedMembers);

        final var actualError = assertThrows(DomainException.class, () -> actualVideo.validate(new ThrowsValidationHandler()));

        assertEquals(expcetedErrorCount, actualError.getErrors().size());
        assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());
    }

    @Test
    void Given_EmptyTitle_When_CallsValidate_Should_ReceiveError() {
        final var expectedTitle = " ";
        final var expectedDescription = """
                Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniôes pessoais.
                Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                Para acessar todas as aulas, lives e desafios, acesse:
                https://imersao.fullcycle.com.br/
                """;
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.0;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var expcetedErrorCount = 1;
        final var expectedErrorMessage = "'title' should not be empty";

        final var actualVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedRating,
                expectedOpened,
                expectedPublished,
                expectedCategories,
                expectedGenres,
                expectedMembers);

        final var actualError = assertThrows(DomainException.class, () -> actualVideo.validate(new ThrowsValidationHandler()));

        assertEquals(expcetedErrorCount, actualError.getErrors().size());
        assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());
    }

    @Test
    void Given_TitleWithLengthGreaterThan255_When_CallsValidate_Should_ReceiveError() {
        final var expectedTitle = """
                Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniôes pessoais.
                Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                Para acessar todas as aulas, lives e desafios, acesse:
                https://imersao.fullcycle.com.br/
                """;
        final var expectedDescription = """
                Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniôes pessoais.
                Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                Para acessar todas as aulas, lives e desafios, acesse:
                https://imersao.fullcycle.com.br/
                """;
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.0;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var expcetedErrorCount = 1;
        final var expectedErrorMessage = "'title' must be between 0 and 255 characteres";

        final var actualVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedRating,
                expectedOpened,
                expectedPublished,
                expectedCategories,
                expectedGenres,
                expectedMembers);

        final var actualError = assertThrows(DomainException.class, () -> actualVideo.validate(new ThrowsValidationHandler()));

        assertEquals(expcetedErrorCount, actualError.getErrors().size());
        assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());
    }

    @Test
    void Given_EmptyDescription_When_CallsValidate_Should_ReceiveError() {
        final var expectedTitle = "System Design Interviews";
        final var expectedDescription = " ";
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.0;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var expcetedErrorCount = 1;
        final var expectedErrorMessage = "'description' should not be empty";

        final var actualVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedRating,
                expectedOpened,
                expectedPublished,
                expectedCategories,
                expectedGenres,
                expectedMembers);

        final var actualError = assertThrows(DomainException.class, () -> actualVideo.validate(new ThrowsValidationHandler()));

        assertEquals(expcetedErrorCount, actualError.getErrors().size());
        assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());
    }

    @Test
    void Given_DescriptionWithLengthGreaterThan4000_When_CallsValidate_Should_ReceiveError() {
        final var expectedTitle = "System Design Interviews";
        final var expectedDescription = """
                Nunca é demais lembrar o peso e o significado destes problemas, uma vez que a complexidade dos estudos efetuados afeta positivamente a correta previsão do sistema de formação de quadros que corresponde às necessidades. Podemos já vislumbrar o modo pelo qual a execução dos pontos do programa obstaculiza a apreciação da importância das diretrizes de desenvolvimento para o futuro. O incentivo ao avanço tecnológico, assim como a determinação clara de objetivos prepara-nos para enfrentar situações atípicas decorrentes do sistema de participação geral.
                Todavia, o fenômeno da Internet estimula a padronização das posturas dos órgãos dirigentes com relação às suas atribuições. Desta maneira, a estrutura atual da organização cumpre um papel essencial na formulação das novas proposições. A prática cotidiana prova que o desenvolvimento contínuo de distintas formas de atuação ainda não demonstrou convincentemente que vai participar na mudança das formas de ação.
                Pensando mais a longo prazo, a mobilidade dos capitais internacionais faz parte de um processo de gerenciamento dos métodos utilizados na avaliação de resultados. As experiências acumuladas demonstram que o novo modelo estrutural aqui preconizado causa impacto indireto na reavaliação dos procedimentos normalmente adotados. Por outro lado, a expansão dos mercados mundiais facilita a criação do fluxo de informações.
                Acima de tudo, é fundamental ressaltar que o início da atividade geral de formação de atitudes acarreta um processo de reformulação e modernização do retorno esperado a longo prazo. Não obstante, a constante divulgação das informações nos obriga à análise da gestão inovadora da qual fazemos parte. Todas estas questões, devidamente ponderadas, levantam dúvidas sobre se a consulta aos diversos militantes assume importantes posições no estabelecimento de alternativas às soluções ortodoxas. Caros amigos, o surgimento do comércio virtual agrega valor ao estabelecimento do processo de comunicação como um todo.
                Ainda assim, existem dúvidas a respeito de como o julgamento imparcial das eventualidades estende o alcance e a importância das direções preferenciais no sentido do progresso. O que temos que ter sempre em mente é que a consolidação das estruturas exige a precisão e a definição dos conhecimentos estratégicos para atingir a excelência. Gostaria de enfatizar que o aumento do diálogo entre os diferentes setores produtivos maximiza as possibilidades por conta dos níveis de motivação departamental. O empenho em analisar a competitividade nas transações comerciais oferece uma interessante oportunidade para verificação de todos os recursos funcionais envolvidos.
                No mundo atual, a necessidade de renovação processual é uma das consequências do remanejamento dos quadros funcionais. É claro que a percepção das dificuldades representa uma abertura para a melhoria dos relacionamentos verticais entre as hierarquias. No entanto, não podemos esquecer que o desafiador cenário globalizado promove a alavancagem do levantamento das variáveis envolvidas. A nível organizacional, a adoção de políticas descentralizadoras não pode mais se dissociar dos paradigmas corporativos.
                É importante questionar o quanto a contínua expansão de nossa atividade possibilita uma melhor visão global das condições financeiras e administrativas exigidas. Neste sentido, o comprometimento entre as equipes talvez venha a ressaltar a relatividade do impacto na agilidade decisória. Evidentemente, a revolução dos costumes auxilia a preparação e a composição das diversas correntes de pensamento. Por conseguinte, o acompanhamento das preferências de consumo desafia a capacidade de equalização dos índices pretendidos. Assim mesmo, a valorização de fatores subjetivos aponta para a melhoria das regras de conduta normativas.
                O cuidado em identificar pontos críticos no entendimento das metas propostas apresenta tendências no sentido de aprovar a manutenção dos modos de operação convencionais. Do mesmo modo, a crescente influência da mídia garante a contribuição de um grupo importante na determinação das condições inegavelmente apropriadas. Percebemos, cada vez mais, que o consenso sobre a necessidade de qualificação deve passar por modificações independentemente do investimento em reciclagem técnica. A certificação de metodologias que nos auxiliam a lidar com a hegemonia do ambiente político pode nos levar a considerar a reestruturação do orçamento setorial.
                A prática cotidiana prova que a consulta aos diversos militantes afeta positivamente a correta previsão do sistema de participação geral. No mundo atual, a expansão dos mercados mundiais obstaculiza a apreciação da importância das diretrizes de desenvolvimento para o futuro. Desta maneira, o consenso sobre a necessidade de qualificação auxilia a preparação e a composição dos níveis de motivação departamental. Acima de tudo, é fundamental ressaltar que a percepção das dificuldades promove a alavancagem das posturas dos órgãos dirigentes com relação às suas atribuições. Caros amigos, o comprometimento entre as equipes cumpre um papel essencial na formulação dos conhecimentos estratégicos para atingir a excelência.
                """;
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.0;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var expcetedErrorCount = 1;
        final var expectedErrorMessage = "'description' must be between 0 and 4000 characteres";

        final var actualVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedRating,
                expectedOpened,
                expectedPublished,
                expectedCategories,
                expectedGenres,
                expectedMembers);

        final var actualError = assertThrows(DomainException.class, () -> actualVideo.validate(new ThrowsValidationHandler()));

        assertEquals(expcetedErrorCount, actualError.getErrors().size());
        assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());
    }

    @Test
    void Given_NullLaunchedAt_When_CallsValidate_Should_ReceiveError() {
        final var expectedTitle = "System Design Interviews";
        final var expectedDescription = """
                Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniôes pessoais.
                Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                Para acessar todas as aulas, lives e desafios, acesse:
                https://imersao.fullcycle.com.br/
                """;
        final Year expectedLaunchedAt = null;
        final var expectedDuration = 120.0;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var expcetedErrorCount = 1;
        final var expectedErrorMessage = "'launchedAt' should not be null";

        final var actualVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedRating,
                expectedOpened,
                expectedPublished,
                expectedCategories,
                expectedGenres,
                expectedMembers);

        final var actualError = assertThrows(DomainException.class, () -> actualVideo.validate(new ThrowsValidationHandler()));

        assertEquals(expcetedErrorCount, actualError.getErrors().size());
        assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());
    }

    @Test
    void Given_NullRating_When_CallsValidate_Should_ReceiveError() {
        final var expectedTitle = "System Design Interviews";
        final var expectedDescription = """
                Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniôes pessoais.
                Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                Para acessar todas as aulas, lives e desafios, acesse:
                https://imersao.fullcycle.com.br/
                """;
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.0;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final Rating expectedRating = null;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var expcetedErrorCount = 1;
        final var expectedErrorMessage = "'rating' should not be null";

        final var actualVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedRating,
                expectedOpened,
                expectedPublished,
                expectedCategories,
                expectedGenres,
                expectedMembers);

        final var actualError = assertThrows(DomainException.class, () -> actualVideo.validate(new ThrowsValidationHandler()));

        assertEquals(expcetedErrorCount, actualError.getErrors().size());
        assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());
    }

}
